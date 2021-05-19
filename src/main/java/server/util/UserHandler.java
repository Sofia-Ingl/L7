package server.util;

import server.Server;
import server.util.constants.DatabaseConstants;
import server.util.constants.QueryConstants;
import shared.serializable.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHandler {

    private final DatabaseManager databaseManager;

    public UserHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public User getUserByName(String name) throws SQLException {
        User user = null;
        PreparedStatement getUserStatement = null;
        try {
            getUserStatement = databaseManager.getPreparedStatement(QueryConstants.SELECT_USER_BY_NAME, false);
            getUserStatement.setString(1, name);
            ResultSet resultSet = getUserStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getString(DatabaseConstants.USER_NAME_COLUMN_IN_USERS), resultSet.getString(DatabaseConstants.USER_PASSWORD_COLUMN_IN_USERS));
            }
        } catch (SQLException e) {
            Server.logger.info("Ошибка при выборке пользователя по имени из базы данных");
            throw e;
        } finally {
            if (getUserStatement != null) databaseManager.closeStatement(getUserStatement);
        }
        return user;
    }

    public boolean findUserByNameAndPass(User user) throws SQLException {
        PreparedStatement getUserStatement = null;
        try {
            getUserStatement = databaseManager.getPreparedStatement(QueryConstants.SELECT_USER_BY_NAME_AND_PASSWORD, false);
            getUserStatement.setString(1, user.getLogin());
            getUserStatement.setString(2, user.getPassword());
            ResultSet resultSet = getUserStatement.executeQuery();
            return resultSet.next();
        } finally {
            if (getUserStatement != null) databaseManager.closeStatement(getUserStatement);
        }
    }

    public boolean insertUser(User user) throws SQLException {
        if (getUserByName(user.getLogin()) != null) return false;
        PreparedStatement insertUserStatement = databaseManager.getPreparedStatement(QueryConstants.INSERT_USER, false);
        insertUserStatement.setString(1, user.getLogin());
        insertUserStatement.setString(2, user.getPassword());
        if (insertUserStatement.executeUpdate() == 0) throw new SQLException();
        return true;
    }
}
