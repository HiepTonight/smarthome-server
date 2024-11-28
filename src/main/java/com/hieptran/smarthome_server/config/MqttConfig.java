package com.hieptran.smarthome_server.config;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
@Configuration
public class MqttConfig {
    @Value("${mqtt.host}")
    private String host;

    @Value("${mqtt.port}")
    private int port;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public Mqtt5AsyncClient mqttClient(){
        final Mqtt5AsyncClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(host)
                .sslWithDefaultConfig()
                .serverPort(port)
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((mqtt5ConnAck, throwable) -> {
                    if (throwable == null) {
                        System.out.println("Kết nối MQTT thành công! mqtt5ConnAck: " + mqtt5ConnAck);
                    } else {
                        System.err.println("Lỗi khi kết nối MQTT: " + throwable.getMessage());
                    }
                });

        return client;
    }
}
