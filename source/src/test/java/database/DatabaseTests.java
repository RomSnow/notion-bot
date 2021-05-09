package database;

import db_storage.DBConnector;
import org.junit.Test;

import java.sql.SQLException;

public class DatabaseTests {
    @Test
    public void test() throws SQLException {
        var connection = DBConnector.createConnection();
        connection.close();
    }
}
