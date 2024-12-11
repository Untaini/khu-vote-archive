package com.example.khuvote;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
//@EnableBatchProcessing
@SpringBootApplication
public class KhuVoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(KhuVoteApplication.class, args);
    }

}
