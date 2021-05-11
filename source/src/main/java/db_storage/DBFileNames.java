package db_storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBFileNames {
    public static boolean addFileRecord(String fileId, String fileName, String adminName) {
        Connection connection = null;
        Statement statement = null;

        var sql = String.format(
                "INSERT INTO names (name, file_id, admin) VALUES ('%s', '%s', '%s')",
                fileName, fileId, adminName);
        try {
            connection = DBConnector.createConnection();
            statement = connection.createStatement();

            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            return false;
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

    public static String getFileName(String id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet reply;

        var sql = String.format(
                "SELECT name FROM names WHERE file_id = '%s'", id
        );

        try {
            connection = DBConnector.createConnection();
            statement = connection.createStatement();

            reply = statement.executeQuery(sql);
            if (!reply.next())
                throw new SQLException();

            return reply.getString("name");

        } catch (SQLException e) {
            return "";
        } finally {
            if (connection != null && statement != null)
                try {
                    connection.close();
                    statement.close();
                } catch (SQLException ignored) {}
        }
    }

    public static boolean removeFileRecord(String fileId){
        return true;
    }
}
