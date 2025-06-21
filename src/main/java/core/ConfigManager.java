package core;// 1. ConfigManager.java


import utils.EncryptionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + CONFIG_FILE, e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getGroqApiKey() {
        return EncryptionUtil.decryptKey(properties.getProperty("groq.api.key"));
    }

    public static String getGroqApiUrl() {
        return properties.getProperty("groq.api.url", "https://api.groq.com/openai/v1/chat/completions");
    }

    public static String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait", "10"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicit.wait", "20"));
    }
}