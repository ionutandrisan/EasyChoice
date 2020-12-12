package com.licenta.andrisan.easychoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EasyChoice {

    public static void main(String[] args) {
        SpringApplication.run(EasyChoice.class, args);
    }

}
