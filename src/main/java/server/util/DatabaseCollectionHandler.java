package server.util;

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

    private final DatabaseManager databaseManager;
    private final UserHandler userHandler;

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

            setScreenwriterQueryParams(insertScreenwriterStatement, screenwriter);

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

    private void setScreenwriterQueryParams(PreparedStatement insertScreenwriterStatement, Person screenwriter) throws SQLException {
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
    }

    public void deleteAllMoviesBelongToUser(User user) throws SQLException {

        PreparedStatement deleteMoviesStatement;
        PreparedStatement deleteScreenwritersStatement;

        databaseManager.setRegulatedCommit();
        Savepoint savepoint = databaseManager.setSavepoint();

        deleteMoviesStatement = databaseManager.getPreparedStatement(QueryConstants.DELETE_MOVIES_BY_USER, false);
        deleteScreenwritersStatement = databaseManager.getPreparedStatement(QueryConstants.DELETE_SCREENWRITERS_WHICH_ARE_NOT_USED, false);

        try {

            deleteMoviesStatement.setString(1, user.getLogin());
            deleteMoviesStatement.executeUpdate();

            deleteScreenwritersStatement.executeUpdate();
            databaseManager.commit();

        } catch (SQLException e) {
            Server.logger.warn("Ошибка при выполнении запросов на удаление принадлежащих пользователю объектов из бд!");
            databaseManager.rollback(savepoint);
            throw e;
        } finally {
            databaseManager.closeStatement(deleteMoviesStatement);
            databaseManager.closeStatement(deleteScreenwritersStatement);
            databaseManager.setAutoCommit();
        }

    }

    public void removeMoviesByScreenwriter(String screenwriterName, User user) throws SQLException {

        PreparedStatement deleteMoviesStatement;
        PreparedStatement selectScreenwriterIds;
        PreparedStatement deleteScreenwritersStatement;

        databaseManager.setRegulatedCommit();
        Savepoint savepoint = databaseManager.setSavepoint();

        selectScreenwriterIds = databaseManager.getPreparedStatement(QueryConstants.SELECT_SCREENWRITER_ID_BY_NAME, false);
        deleteMoviesStatement = databaseManager.getPreparedStatement(QueryConstants.DELETE_MOVIES_BY_SCREENWRITER_ID_AND_USER, false);
        deleteScreenwritersStatement = databaseManager.getPreparedStatement(QueryConstants.DELETE_SCREENWRITERS_WHICH_ARE_NOT_USED, false);

        try {

            selectScreenwriterIds.setString(1, screenwriterName);
            ResultSet resultSet = selectScreenwriterIds.executeQuery();
            int screenwriterId;

            while (resultSet.next()) {
                screenwriterId = resultSet.getInt(DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_SCREENWRITERS);
                deleteMoviesStatement.setInt(1, screenwriterId);
                deleteMoviesStatement.setString(2, user.getLogin());
                deleteMoviesStatement.executeUpdate();
            }

            deleteScreenwritersStatement.executeUpdate();
            databaseManager.commit();


        } catch (SQLException e) {
            Server.logger.warn("Ошибка при выполнении запросов на удаление фильмов по сценаристу из бд!");
            databaseManager.rollback(savepoint);
            throw e;
        } finally {
            databaseManager.closeStatement(selectScreenwriterIds);
            databaseManager.closeStatement(deleteMoviesStatement);
            databaseManager.closeStatement(deleteScreenwritersStatement);
            databaseManager.setAutoCommit();
        }
    }

    public void deleteMovieById(int id, User user) throws SQLException {

        PreparedStatement deleteMovieByIdStatement = databaseManager.getPreparedStatement(QueryConstants.DELETE_MOVIES_BY_ID_AND_USER, false);

        try {
            deleteMovieByIdStatement.setInt(1, id);
            deleteMovieByIdStatement.setString(2, user.getLogin());
            deleteMovieByIdStatement.executeUpdate();

        } catch (SQLException e) {
            Server.logger.warn("Ошибка при удалении фильма по id из бд!");
            throw e;
        } finally {
            databaseManager.closeStatement(deleteMovieByIdStatement);
        }
    }

    public void removeGreaterMovies(Movie movieToCompareWith, User user, CollectionStorage collectionStorage) throws SQLException {

        databaseManager.setRegulatedCommit();
        Savepoint savepoint = databaseManager.setSavepoint();
        try {
            for (Movie m : collectionStorage.getCollection()) {
                if (movieToCompareWith.compareTo(m) < 0) {
                    deleteMovieById(m.getId(), user);
                }
            }
            databaseManager.commit();

        } catch (SQLException e) {
            Server.logger.warn("Ошибка при выполнении запросов на удаление фильмов, превосходящих заданный, из бд!");
            databaseManager.rollback(savepoint);
            throw e;
        } finally {
            databaseManager.setAutoCommit();
        }
    }

    public void updateMovieById(int id, Movie movie, User user) throws SQLException {

        PreparedStatement selectScreenwriterIdStatement = databaseManager.getPreparedStatement(QueryConstants.SELECT_SCREENWRITER_ID_BY_MOVIE_ID_AND_USER, false);
        PreparedStatement countScreenwriterUsages = databaseManager.getPreparedStatement(QueryConstants.COUNT_SCREENWRITER_USAGES, false);
        PreparedStatement createNewScreenwriter = databaseManager.getPreparedStatement(QueryConstants.INSERT_SCREENWRITER, true);
        PreparedStatement updateScreenwriter = databaseManager.getPreparedStatement(QueryConstants.UPDATE_SCREENWRITER_BY_ID, false);
        PreparedStatement updateMovie = databaseManager.getPreparedStatement(QueryConstants.UPDATE_MOVIE_BY_ID, false);

        databaseManager.setRegulatedCommit();
        Savepoint savepoint = databaseManager.setSavepoint();
        try {

            int scrId;

            selectScreenwriterIdStatement.setInt(1, id);
            selectScreenwriterIdStatement.setString(2, user.getLogin());
            ResultSet resultSet = selectScreenwriterIdStatement.executeQuery();

            if (resultSet.next()) {
                scrId = resultSet.getInt(DatabaseConstants.SCREENWRITER_ID_COLUMN_IN_MOVIES);
            } else return;

            countScreenwriterUsages.setInt(1, scrId);
            resultSet = countScreenwriterUsages.executeQuery();

            if (resultSet.next()) {
                int counts = resultSet.getInt(1);

                Person newScreenwriter = movie.getScreenwriter();
                if (counts <= 1) {

                    setScreenwriterQueryParams(updateScreenwriter, newScreenwriter);
                    updateScreenwriter.setInt(5, scrId);

                    updateScreenwriter.executeUpdate();

                } else {
                    Person previousScreenwriter = getScreenwriterById(scrId);
                    if (!previousScreenwriter.equals(newScreenwriter)) {

                        setScreenwriterQueryParams(createNewScreenwriter, newScreenwriter);
                        createNewScreenwriter.executeUpdate();

                        ResultSet generatedKeys = createNewScreenwriter.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            scrId = generatedKeys.getInt(1);
                        } else throw new SQLException();

                    }

                }

                updateMovie.setString(1, movie.getName());
                updateMovie.setFloat(2, movie.getCoordinates().getX());
                updateMovie.setInt(3, movie.getCoordinates().getY());
                updateMovie.setInt(4, movie.getOscarsCount());
                updateMovie.setLong(5, movie.getGoldenPalmCount());
                updateMovie.setString(6, movie.getTagline());
                updateMovie.setInt(7, scrId);
                updateMovie.setString(8, movie.getGenre().toString());
                updateMovie.setInt(9, id);
                updateMovie.setString(10, user.getLogin());

                if (updateMovie.executeUpdate() == 0) throw new SQLException();

            }

            databaseManager.commit();

        } catch (SQLException e) {
            Server.logger.warn("Ошибка при выполнении запросов на изменение фильма по id в бд!");
            databaseManager.rollback(savepoint);
            throw e;
        } finally {
            databaseManager.closeStatement(selectScreenwriterIdStatement);
            databaseManager.closeStatement(countScreenwriterUsages);
            databaseManager.closeStatement(createNewScreenwriter);
            databaseManager.closeStatement(updateScreenwriter);
            databaseManager.closeStatement(updateMovie);
            databaseManager.setAutoCommit();
        }
    }

}
