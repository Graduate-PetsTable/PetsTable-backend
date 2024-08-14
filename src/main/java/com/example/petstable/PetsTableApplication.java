package com.example.petstable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetsTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetsTableApplication.class, args);
    }

}
