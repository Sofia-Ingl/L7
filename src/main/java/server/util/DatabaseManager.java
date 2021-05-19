package server.util;

import server.Server;
import server.util.constants.QueryConstants;

import java.sql.*;

public class DatabaseManager {

    private final String DRIVER = "org.postgresql.Driver";

    private final String url;
    private final String globalUser;

    private Connection connection;

    public DatabaseManager(String url, String globalUser, String password) {
        this.url = url;
        this.globalUser = globalUser;
        try {
            establishConnectionWithDatabase(password);
            createTablesIfMissed();
        } catch (SQLException e) {
            Server.logger.error("Возникла ошибка соединения с базой данных. Будет осуществлен аварийный выход из сервера");
            System.exit(1);
        }
    }


    private void establishConnectionWithDatabase(String password) throws SQLException {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(url, globalUser, password);
            Server.logger.info("Соединение с базой данных установлено");
        } catch (ClassNotFoundException | SQLException e) {
            Server.logger.error("Ошибка при установлении соединения с базой данных");
            Server.logger.error(e.getMessage());
            for (StackTraceElement element :
                    e.getStackTrace()) {
                Server.logger.error(element.toString());
            }
            throw new SQLException();
        }
    }

    public void closeConnectionWithDatabase() {
        if (connection == null) return;
        try {
            connection.close();
            Server.logger.info("Соединение с базой данных закрыто");
        } catch (SQLException e) {
            Server.logger.error("Ошибка при закрытии соединения с базой данных");
            Server.logger.error(e.getMessage());
            for (StackTraceElement element :
                    e.getStackTrace()) {
                Server.logger.error(element.toString());
            }
        }
    }


    public PreparedStatement getPreparedStatement(String pattern, boolean generatedKeysExpected) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(pattern, generatedKeysExpected ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
            return statement;
        } catch (SQLException e) {
            Server.logger.error("Ошибка при подготовке SQL запроса");
            Server.logger.error(e.getMessage());
            for (StackTraceElement element :
                    e.getStackTrace()) {
                Server.logger.error(element.toString());
            }
        }
        return null;
    }

    public void closeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablesIfMissed() throws SQLException {
        Statement statement = null;
        try {

            statement = connection.createStatement();
            statement.execute(QueryConstants.SCREENWRITER_ID_SEQUENCE_CREATOR);
            statement.execute(QueryConstants.MOVIE_ID_SEQUENCE_CREATOR);
            statement.execute(QueryConstants.SCREENWRITER_TABLE_CREATOR);
            statement.execute(QueryConstants.USER_TABLE_CREATOR);
            statement.execute(QueryConstants.MOVIE_TABLE_CREATOR);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (statement != null) {
                closeStatement(statement);
            }
        }
    }

    public void setAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            Server.logger.warn("Ошибка при установке AutoCommit режима базы данных");
            e.printStackTrace();
        }
    }

    public void setRegulatedCommit() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            Server.logger.warn("Ошибка при изменении режима работы с базой данных");
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            Server.logger.warn("Ошибка при отмене изменений");
            e.printStackTrace();
        }
    }

    public Savepoint setSavepoint() {
        try {
            return connection.setSavepoint();
        } catch (SQLException e) {
            Server.logger.warn("Ошибка при отмене изменений");
            e.printStackTrace();
        }
        return null;
    }


}
