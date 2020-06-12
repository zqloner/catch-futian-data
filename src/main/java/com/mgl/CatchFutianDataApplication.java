package com.mgl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CatchFutianDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatchFutianDataApplication.class, args);
    }

}
