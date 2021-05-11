package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static Config instance;
    private final Properties prop;

    public static Config getConfig() {
        if (instance == null) {
            var configPath = Paths.get("source", "config", "config.conf");
            instance = new Config(configPath);
        }
        return instance;
    }

    private Config(Path path) {
        prop = new Properties();

        InputStream is = null;
        try {
            is = new FileInputStream(path.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        }
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String configSetting) {
        return prop.getProperty(configSetting);
    }
}
