package it.tinna.smartdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SmartDocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartDocApplication.class, args);
    }

}

