package server.util;

import com.google.gson.annotations.Expose;
import server.Server;
import server.util.constants.DatabaseConstants;
import server.util.constants.QueryConstants;
import shared.data.*;
import shared.serializable.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        ZonedDateTime creationDate = ZonedDateTime.of(resultSet.getTimestamp(DatabaseConstants.CREATION_DATE_COLUMN_IN_MOVIES).toLocalDateTime(), ZoneId.of(DatabaseConstants.CREATION_DATE_ZONE_COLUMN_IN_MOVIES));
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
                screenwriter = new Person(
                        resultSet.getString(DatabaseConstants.SCREENWRITER_NAME_COLUMN_IN_SCREENWRITERS),
                        resultSet.getLong(DatabaseConstants.HEIGHT_COLUMN_IN_SCREENWRITERS),
                        Color.valueOf(resultSet.getString(DatabaseConstants.EYE_COLOR_COLUMN_IN_SCREENWRITERS)),
                        Country.valueOf(resultSet.getString(DatabaseConstants.NATION_COLUMN_IN_SCREENWRITERS))
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

    public UserHandler getUserHandler() {
        return userHandler;
    }
}
