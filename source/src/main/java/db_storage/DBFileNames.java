package db_storage;

import handler.InvalidIdException;
import org.apache.commons.codec.digest.DigestUtils;

public class DBFileNames {
    private final String TABLE_NAME = "names";
    private final String EMPTY_PWD = DigestUtils.md5Hex("");

    public void addFileRecord(String fileId, String fileName, String adminName) {
        var sql = String.format(
                "INSERT INTO %s (name, file_id, admin, password) VALUES ('%s', '%s', '%s', '%s')",
                TABLE_NAME, fileName, fileId, adminName, EMPTY_PWD);
        try {
            DBConnector.updateBySQL(sql);
        } catch (InvalidIdException ignore) {}
    }

    public String getNameById(String id) throws InvalidIdException {
        var sql = String.format(
                "SELECT name FROM %s WHERE file_id = '%s'", TABLE_NAME, id
        );
        return DBConnector.getBySQL(sql, "name");
    }

    public String getIdByName(String name) throws InvalidIdException {
        var sql = String.format(
                "SELECT file_id FROM %s WHERE name = '%s'", TABLE_NAME, name
        );
        return DBConnector.getBySQL(sql, "file_id");
    }

    public void removeFileRecordById(String fileId) throws InvalidIdException {
        var sql = String.format(
                "DELETE FROM %s WHERE file_id = '%s'", TABLE_NAME, fileId
        );

        DBConnector.updateBySQL(sql);
    }
}
