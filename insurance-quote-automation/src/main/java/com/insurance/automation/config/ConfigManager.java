package com.insurance.automation.config;

import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;

/**
 * Thread-safe singleton provider for {@link EnvironmentConfig}.
 */
public final class ConfigManager {

    private static volatile EnvironmentConfig config;

    private ConfigManager() {
    }

    /**
     * Retrieves the initialized {@link EnvironmentConfig} instance.
     *
     * @return singleton environment configuration.
     */
    public static EnvironmentConfig getConfig() {
        if (config == null) {
            synchronized (ConfigManager.class) {
                if (config == null) {
                    final String env = System.getProperty("env", "uat");
                    final Properties properties = new Properties();
                    properties.setProperty("env", env);
                    config = ConfigFactory.create(EnvironmentConfig.class, properties);
                }
            }
        }
        return config;
    }
}
