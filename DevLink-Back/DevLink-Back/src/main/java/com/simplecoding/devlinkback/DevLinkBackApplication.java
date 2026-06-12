package com.simplecoding.devlinkback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DevLinkBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevLinkBackApplication.class, args);
    }

}