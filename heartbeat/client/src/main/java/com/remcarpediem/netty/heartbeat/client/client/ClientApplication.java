package com.remcarpediem.netty.heartbeat.client.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication {

    @Autowired
    private HeartbeatClient client;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
