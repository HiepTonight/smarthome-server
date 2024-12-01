package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.dto.requests.SensorDataRequest;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MqttService {
    private final Mqtt5AsyncClient client;

    private final SensorDataService sensorDataService;

    @Value("${mqtt.device.topic}")
    private String topic;

    @PostConstruct
    public void MqttService1() {
        ObjectMapper objectMapper = new ObjectMapper();

        client.subscribeWith()
                .topicFilter("sensorData")
                .callback(publish -> {
                    publish.getPayload().ifPresent(payload -> {
                        // Chuyển ByteBuffer sang chuỗi UTF-8
//                        String message = StandardCharsets.UTF_8.decode(payload).toString();
//                        System.out.println("Received Data: " + message);
                        byte[] data = new byte[payload.remaining()];
                        payload.get(data);
                        try {
                            SensorDataRequest sensorDataRequest = objectMapper.readValue(data, SensorDataRequest.class);
                            sensorDataService.saveSensorData(sensorDataRequest);
                            System.out.println("Parsed Sensor Data: " + sensorDataRequest);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    });
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        // Handle failure to subscribe
                    } else {
                        System.out.println("Subcribe thành công topic");
                        // Handle successful subscription,   e.g. logging or incrementing a metric
                    }
                });
    }

    public void publish(String message) {
        client.publishWith()
                .topic(topic)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .send()
                .whenComplete((mqtt5Publish, throwable) -> {
                    if (throwable != null) {
                        // Handle failure to publish
                        System.err.println("Lỗi khi publish: " + throwable.getMessage());
                    } else {
                        // Handle successful publish, e.g. logging or incrementing a metric
                        System.out.println("Publish thành công: " + mqtt5Publish);
                    }
                });
    }

}
