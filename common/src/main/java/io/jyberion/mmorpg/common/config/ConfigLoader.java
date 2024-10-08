package io.jyberion.mmorpg.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static Properties properties = new Properties();

    public static void load(String filePath) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                throw new IOException("Configuration file not found: " + filePath);
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration file: " + filePath);
        }
    }

    public static void loadProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}