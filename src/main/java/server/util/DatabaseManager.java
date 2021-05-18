package server.util;

import server.Server;

import java.sql.*;

public class DatabaseManager {

    private final String DRIVER = "org.postgresql.Driver";

    private final String url;
    private final String globalUser;

    private Connection connection;

    public DatabaseManager(String url, String globalUser, String password) {

        this.url = url;
        this.globalUser = globalUser;
        establishConnectionWithDatabase(password);

    }


    private void establishConnectionWithDatabase(String password) {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(url, globalUser, password);
            Server.logger.info("Соединение с базой данных установлено");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Server.logger.error("Ошибка при установлении соединения с базой данных");
            Server.logger.error(e.getMessage());
            for (StackTraceElement element:
                 e.getStackTrace()) {
                Server.logger.error(element.toString());
            }
        }
    }


    public PreparedStatement getPreparedStatement(String pattern, boolean generatedKeysExpected) throws SQLException {

        PreparedStatement statement;
        try {

            statement = connection.prepareStatement(pattern, generatedKeysExpected ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
            return statement;

        } catch (SQLException e) {

            Server.logger.error("Ошибка при подготовке SQL запроса");
            Server.logger.error(e.getMessage());
            for (StackTraceElement element:
                    e.getStackTrace()) {
                Server.logger.error(element.toString());
            }
            throw e;
        }

    }


}
