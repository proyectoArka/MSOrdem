package com.arka.MSOrden;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsOrdenApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsOrdenApplication.class, args);
    }
}