package com.individuals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.feign.client")
public class IndividualsApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndividualsApplication.class, args);
    }
}