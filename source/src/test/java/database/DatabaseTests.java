package database;

import db_storage.DBConnector;
import db_storage.DBFileNames;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class DatabaseTests {
    @Test
    public void testDBConnector() throws SQLException {
        var connection = DBConnector.createConnection();
        connection.close();
    }

    @Test
    public void testDBFileNames() {
        var namesDb = new DBFileNames();

        namesDb.addFileRecord("test_id", "test_name", "test_admin");

        var testId = namesDb.getIdByName("test_name");
        var testName = namesDb.getNameById("test_id");

        Assert.assertEquals(testName, "test_name");
        Assert.assertEquals(testId, "test_id");

        namesDb.removeFileRecordById("test_id");
    }
}
