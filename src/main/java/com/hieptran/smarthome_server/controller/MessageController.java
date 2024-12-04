package com.hieptran.smarthome_server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

public class MessageController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        return "Hello, " + message + "!";
    }
}
