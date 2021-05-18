package db_storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import handler.InvalidIdException;

public class DBFileNames {
    private final String TABLE_NAME = "names";

    public void addFileRecord(String fileId, String fileName, String adminName) {
        var sql = String.format(
                "INSERT INTO %s (name, file_id, admin) VALUES ('%s', '%s', '%s')",
                TABLE_NAME, fileName, fileId, adminName);
        try {
            updateBySQL(sql);
        } catch (InvalidIdException ignore) {}
    }

    public String getNameById(String id) throws InvalidIdException {
        var sql = String.format(
                "SELECT name FROM %s WHERE file_id = '%s'", TABLE_NAME, id
        );
        return getBySQL(sql, "name");
    }

    public String getIdByName(String name) throws InvalidIdException {
        var sql = String.format(
                "SELECT file_id FROM %s WHERE name = '%s'", TABLE_NAME, name
        );
        return getBySQL(sql, "file_id");
    }

    public void removeFileRecordById(String fileId) throws InvalidIdException {
        var sql = String.format(
                "DELETE FROM %s WHERE file_id = '%s'", TABLE_NAME, fileId
        );

        updateBySQL(sql);
    }

    private String getBySQL(String sql, String dataToGet) throws InvalidIdException {
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

    private void updateBySQL(String sql) throws InvalidIdException{
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DBConnector.createConnection();
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
}
