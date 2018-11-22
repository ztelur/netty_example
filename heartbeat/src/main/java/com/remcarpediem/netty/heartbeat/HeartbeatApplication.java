package com.remcarpediem.netty.heartbeat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeartbeatApplication implements CommandLineRunner {

    @Value("${n.port}")
    private int port;

    @Value("${n.url}")
    private String url;


    public static void main(String[] args) {
        SpringApplication.run(HeartbeatApplication.class, args);
    }




    @Override
    public void run(String... args) throws Exception {

    }
}

