package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.clients.UserClient;
import ru.practicum.clients.event.AdminEventClient;

@EnableFeignClients(clients = {UserClient.class, AdminEventClient.class})
@SpringBootApplication
public class RequestServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApp.class, args);
    }

}