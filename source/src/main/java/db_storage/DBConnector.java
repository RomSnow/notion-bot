package db_storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static config.Config.getConfig;

public class DBConnector {

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                getConfig().getString("db.url"),
                getConfig().getString("db.user"),
                getConfig().getString("db.password"));
    }
}
