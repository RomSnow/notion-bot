package db_storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private static String URL;
    private static String USER;
    private static String PWD;
    private static final String confFilePath = "conf/db_data.conf";

    private static void readConf() {
        var prop = new Properties();
        try {
            prop.load(new FileInputStream(confFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        URL = prop.getProperty("URL");
        USER = prop.getProperty("USER");
        PWD = prop.getProperty("PWD");
    }

    public static Connection createConnection() throws SQLException {
        if (URL == null)
            readConf();
        return DriverManager.getConnection(URL, USER, PWD);
    }
}
