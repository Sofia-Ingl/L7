package server.util;

import com.google.gson.annotations.Expose;
import server.Server;
import server.util.constants.DatabaseConstants;
import server.util.constants.QueryConstants;
import shared.data.*;
import shared.serializable.User;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;

public class DatabaseCollectionHandler {

    private DatabaseManager databaseManager;
    private UserHandler userHandler;

    public DatabaseCollectionHandler(DatabaseManager databaseManager, UserHandler userHandler) {
        this.databaseManager = databaseManager;
        this.userHandler = userHandler;
    }

    public LinkedHashSet<Movie> loadCollectionFromDatabase() throws SQLException {
        LinkedHashSet<Movie> collection = new LinkedHashSet<>();
        PreparedStatement getThemAllPrepared = null;
        try {
            getThemAllPrepared = databaseManager.getPreparedStatement(QueryConstants.SELECT_ALL_MOVIES, false);
            ResultSet resultSet = getThemAllPrepared.executeQuery();
            while (resultSet.next()) {
                collection.add(getMovieFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            Server.logger.error("Ошибка при загрузке коллекции из базы данных");
            Server.logger.error(e.getMessage());
            for (StackTraceElement element :
                    e.getStackTrace()) {
                Server.logger.error(element.toString());
            }
            throw e;
        } finally {
            if (getThemAllPrepared != null) databaseManager.closeStatement(getThemAllPrepared);
        }
        return collection;
    }

    private Movie getMovieFromResultSet(ResultSet resultSet) throws SQLException {

        int id = resultSet.getInt(DatabaseConstants.MOVIE_ID_COLUMN_IN_MOVIES);
        String name = resultSet.getString(DatabaseConstants.MOVIE_NAME_COLUMN_IN_MOVIES);
        Coordinates coordinates = new Coordinates(resultSet.getFloat(DatabaseConstants.X_COORDINATE_COLUMN_IN_MOVIES), resultSet.getInt(DatabaseConstants.Y_COORDINATE_COLUMN_IN_MOVIES));
        ZonedDateTime creationDate = ZonedDateTime.of(resultSet.getTimestamp(DatabaseConstants.CREATION_DATE_COLUMN_IN_MOVIES).toLocalDateTime(), ZoneId.of(resultSet.getString(DatabaseConstants.CREATION_DATE_ZONE_COLUMN_IN_MOVIES)));
        int oscars = resultSet.getInt(DatabaseConstants.OSCARS_COUNT_COLUMN_IN_MOVIES);
        long palms = resultSet.getLong(DatabaseConstants.PALMS_COUNT_COLUMN_IN_MOVIES);
        String tagline = resultSet.getString(DatabaseConstants.TAGLINE_COLUMN_IN_MOVIES);
        MovieGenre genre = MovieGenre.valueOf(resultSet.getString(DatabaseConstants.GENRE_COLUMN_IN_MOVIES));
        Person screenwriter = getScreenwriterById(resultSet.getInt(DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES));
        User user = userHandler.getUserByName(resultSet.getString(DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES));

        if (screenwriter == null || user == null) {
            Server.logger.error("База данных неверно сконфигурирована | Не все связи между таблицами установлены");
            throw new SQLException();
        }
        return new Movie(id, name, coordinates, creationDate, oscars, palms, tagline, genre, screenwriter, user);
    }


    public Person getScreenwriterById(int id) throws SQLException {
        Person screenwriter = null;
        PreparedStatement getScreenwriterStatement = null;
        try {
            getScreenwriterStatement = databaseManager.getPreparedStatement(QueryConstants.SELECT_SCREENWRITER_BY_ID, false);
            getScreenwriterStatement.setInt(1, id);
            ResultSet resultSet = getScreenwriterStatement.executeQuery();
            if (resultSet.next()) {
                String eyeColor = resultSet.getString(DatabaseConstants.EYE_COLOR_COLUMN_IN_SCREENWRITERS);
                String nationality = resultSet.getString(DatabaseConstants.NATION_COLUMN_IN_SCREENWRITERS);
                screenwriter = new Person(
                        resultSet.getString(DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS),
                        resultSet.getLong(DatabaseConstants.HEIGHT_COLUMN_IN_SCREENWRITERS),
                        (eyeColor == null) ? null : Color.valueOf(eyeColor),
                        (nationality == null) ? null : Country.valueOf(nationality)
                );
            }
        } catch (SQLException e) {
            Server.logger.info("Ошибка при выборке режиссера по id из базы данных");
            throw e;
        } finally {
            if (getScreenwriterStatement != null) databaseManager.closeStatement(getScreenwriterStatement);
        }
        return screenwriter;
    }

    public Movie addNewMovie(Movie movie, User owner) throws SQLException {

        PreparedStatement insertMovieStatement;
        PreparedStatement insertScreenwriterStatement;

        databaseManager.setRegulatedCommit();
        Savepoint savepoint = databaseManager.setSavepoint();

        insertScreenwriterStatement = databaseManager.getPreparedStatement(QueryConstants.INSERT_SCREENWRITER, true);
        insertMovieStatement = databaseManager.getPreparedStatement(QueryConstants.INSERT_MOVIE, true);

        try {

            Person screenwriter = movie.getScreenwriter();

            insertScreenwriterStatement.setString(1, screenwriter.getName());
            insertScreenwriterStatement.setLong(2, screenwriter.getHeight());
            if (screenwriter.getEyeColor() != null) {
                insertScreenwriterStatement.setString(3, screenwriter.getEyeColor().toString());
            } else {
                insertScreenwriterStatement.setNull(3, Types.LONGVARCHAR);
            }
            if (screenwriter.getNationality() != null) {
                insertScreenwriterStatement.setString(4, screenwriter.getNationality().toString());
            } else {
                insertScreenwriterStatement.setNull(4, Types.LONGVARCHAR);
            }

            if (insertScreenwriterStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedScreenwriterKeys = insertScreenwriterStatement.getGeneratedKeys();

            int screenwriterId;
            if (generatedScreenwriterKeys.next()) {
                screenwriterId = generatedScreenwriterKeys.getInt(1);
            } else throw new SQLException();

            ZonedDateTime creationDate = ZonedDateTime.now();
            Coordinates coordinates = movie.getCoordinates();

            insertMovieStatement.setString(1, movie.getName());
            insertMovieStatement.setFloat(2, coordinates.getX());
            insertMovieStatement.setInt(3, coordinates.getY());
            insertMovieStatement.setTimestamp(4, Timestamp.valueOf(creationDate.toLocalDateTime()));
            insertMovieStatement.setString(5, creationDate.getZone().toString());
            insertMovieStatement.setInt(6, movie.getOscarsCount());
            insertMovieStatement.setLong(7, movie.getGoldenPalmCount());
            insertMovieStatement.setString(8, movie.getTagline());
            insertMovieStatement.setString(9, movie.getGenre().toString());
            insertMovieStatement.setInt(10, screenwriterId);
            if (!userHandler.findUserByNameAndPass(owner)) throw new SQLException();
            insertMovieStatement.setString(11, owner.getLogin());

            if (insertMovieStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet preparedMovieKeys = insertMovieStatement.getGeneratedKeys();
            int movieId;
            if (preparedMovieKeys.next()) {
                movieId = preparedMovieKeys.getInt(1);
            } else throw new SQLException();

            movie.setCreationDate(creationDate);
            movie.setId(movieId);

            databaseManager.commit();
            return movie;

            /*
        public static final String INSERT_MOVIE = "INSERT INTO " + DatabaseConstants.MOVIE_TABLE
            + " (" + DatabaseConstants.MOVIE_NAME_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.X_COORDINATE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.Y_COORDINATE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.CREATION_DATE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.CREATION_DATE_ZONE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.OSCARS_COUNT_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.PALMS_COUNT_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.TAGLINE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.GENRE_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES + ", "
            + DatabaseConstants.USER_NAME_COLUMN_IN_MOVIES + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

     */


        } catch (SQLException e) {
            Server.logger.warn("Ошибка при выполнении запросов на добавление нового объекта в бд!");
            databaseManager.rollback(savepoint);
            throw e;
        } finally {
            databaseManager.closeStatement(insertMovieStatement);
            databaseManager.closeStatement(insertScreenwriterStatement);
            databaseManager.setAutoCommit();
        }

    }

    public UserHandler getUserHandler() {
        return userHandler;
    }
}
