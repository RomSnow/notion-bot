package db_storage;

import handler.InvalidIdException;

import java.sql.*;

import static config.Config.getConfig;

public class DBConnector {

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                getConfig().getString("db.url"),
                getConfig().getString("db.user"),
                getConfig().getString("db.password"));
    }

    public static void updateBySQL(String sql) throws InvalidIdException {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = createConnection();
            statement = connection.createStatement();

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new InvalidIdException(sql);
        } finally {
            if (connection != null && statement != null) {
                try {
                    connection.close();
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
        }

    }

    public static String getBySQL(String sql, String dataToGet) throws InvalidIdException {
        Connection connection = null;
        Statement statement = null;
        ResultSet reply;

        try {
            connection = DBConnector.createConnection();
            statement = connection.createStatement();

            reply = statement.executeQuery(sql);
            if (!reply.next())
                throw new SQLException();

            return reply.getString(dataToGet);

        } catch (SQLException e) {
            throw new InvalidIdException(sql);
        } finally {
            if (connection != null && statement != null)
                try {
                    connection.close();
                    statement.close();
                } catch (SQLException ignored) {
                }
        }
    }
}
