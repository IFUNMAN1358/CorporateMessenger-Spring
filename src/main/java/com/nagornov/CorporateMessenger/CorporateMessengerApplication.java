package com.nagornov.CorporateMessenger;

import com.nagornov.CorporateMessenger.infrastructure.configuration.properties.SystemProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CorporateMessengerApplication {

    public static void main(String[] args) {

        SystemProperties.initSystemProperties();

        SpringApplication.run(CorporateMessengerApplication.class, args);
    }

}
