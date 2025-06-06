package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.clients.UserClient;
import ru.practicum.clients.event.AdminEventClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class, AdminEventClient.class, })
public class CommentServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApp.class, args);
    }

}