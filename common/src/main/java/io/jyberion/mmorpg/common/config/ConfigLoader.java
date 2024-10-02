package io.jyberion.mmorpg.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    public static void load(String fileName) throws IOException {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find configuration file: " + fileName);
            }
            properties.load(input);

            // Set system properties for Hibernate if needed
            System.setProperty("database.url", properties.getProperty("database.url"));
            System.setProperty("database.username", properties.getProperty("database.username"));
            System.setProperty("database.password", properties.getProperty("database.password"));
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
