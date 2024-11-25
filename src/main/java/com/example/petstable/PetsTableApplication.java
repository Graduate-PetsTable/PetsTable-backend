package com.example.petstable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PetsTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetsTableApplication.class, args);
    }

}
