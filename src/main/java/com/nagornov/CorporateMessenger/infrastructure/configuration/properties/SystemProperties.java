package com.nagornov.CorporateMessenger.infrastructure.configuration.properties;

import io.github.cdimascio.dotenv.Dotenv;

public class SystemProperties {

    public static void initSystemProperties() {
        try {

            // General

            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

            // Env

            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize system properties", e);
        }

    }

}
