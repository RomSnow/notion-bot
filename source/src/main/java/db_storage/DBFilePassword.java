package db_storage;

import handler.InvalidIdException;

public class DBFilePassword {
    private static final String TABLE_NAME = "names";

    public static void setPassword(String password, String id) throws InvalidIdException {
        var sql = "UPDATE %s SET password = '%s' WHERE file_id = '%s'"
                .formatted(TABLE_NAME, password, id);
        DBConnector.updateBySQL(sql);
    }

    public static String getPassword(String id) throws InvalidIdException {
        var sql = "SELECT password FROM %s WHERE file_id = '%s'"
                .formatted(TABLE_NAME, id);
        return DBConnector.getBySQL(sql, "password");
    }
}
