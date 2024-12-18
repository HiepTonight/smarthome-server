package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.config.CacheConfig;
import com.hieptran.smarthome_server.dto.requests.SensorDataRequest;
import com.hieptran.smarthome_server.model.DeviceAuto;
import com.hieptran.smarthome_server.model.Home;
import com.hieptran.smarthome_server.model.HomeOption;
import com.hieptran.smarthome_server.model.TempAutoOption;
import com.hieptran.smarthome_server.repository.HomeRepository;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MqttService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Mqtt5AsyncClient client;

    private final SensorDataService sensorDataService;

    private final HomeRepository homeRepository;

    private final CacheConfig cache;

    @Value("${mqtt.topic.homepod}")
    private String homePodTopic;

    @Value("${mqtt.topic.device}")
    private String deviceControlTopic;

    @PostConstruct
    public void subscribeAllHomePods() throws Exception {
        List<Home> homePodIds = homeRepository.findAll();
        for (String homePodId : homePodIds.stream().map(Home::getHomePodId).toList()) {
            String topic = String.format("homePod/%s", homePodId);
            client.subscribeWith()
                    .topicFilter(topic)
                    .callback(publish -> {
                        try {
                            handleMessage(publish);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .send()
                    .whenComplete((subAck, throwable) -> {
                        if (throwable != null) {
                            System.err.println("Failed to subscribe to topic: " + topic);
                        } else {
                            System.out.println("Successfully subscribed to topic: " + topic);
                        }
                    });
        }
    }

    @PostConstruct
    public void MqttService1() {

        client.subscribeWith()
                .topicFilter("sensorData")
                .callback(publish -> {
                    try {
                        handleMessage(publish);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {

                    } else {
                        System.out.println("Subcribe thành công topic");
                        // Handle successful subscription,   e.g. logging or incrementing a metric
                    }
                });
    }

    public void subcribe(String topic) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    try {
                        handleMessage(publish);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {

                    } else {
                        System.out.println("Subcribe thành công topic");
                        // Handle successful subscription,   e.g. logging or incrementing a metric
                    }
                });
    }

    public void unSubcribe(String topic) {
        client.unsubscribeWith()
                .topicFilter(topic)
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Lỗi khi unsubcribe: " + throwable.getMessage());
                    } else {
                        System.out.println("Unsubcribe thành công topic");
                        // Handle successful subscription,   e.g. logging or incrementing a metric
                    }
                });
    }

    private void handleMessage(Mqtt5Publish publish) throws Exception {
        String topic = publish.getTopic().toString();
        byte[] payload = publish.getPayloadAsBytes();
        String homePodId = extractHomePodIdFromTopic(topic);
        processMessage(homePodId, payload);
    }

    private String extractHomePodIdFromTopic(String topic) {
        String[] segments = topic.split("homePod/");
        return segments[1]; //  format "homePod/{homePodId}"
    }


    private void processMessage(String homePodId, byte[] payload) throws Exception {

        Home home = homeRepository.findByHomePodId(homePodId);

        if (home == null) {
            throw new Exception("Home not found");
        }

        SensorDataRequest sensorDataRequest = objectMapper.readValue(payload, SensorDataRequest.class);
        sensorDataRequest.setHomePodId(homePodId);

        HomeOption homeOption = (HomeOption) cache.get(homePodId);

        if (homeOption != null) {
            processSensorData(sensorDataRequest.getTemp(), (Number) homeOption.getTempAutoOption().getGreaterThan(),
                    (Number) homeOption.getTempAutoOption().getLessThan(),
                    homeOption.getTempAutoOption().getHighDevices(),
                    homeOption.getTempAutoOption().getLowDevices(),
                    homePodId);
            processSensorData(sensorDataRequest.getHumi(),
                    (Number) homeOption.getHumiAutoOption().getGreaterThan(),
                    (Number) homeOption.getHumiAutoOption().getLessThan(),
                    homeOption.getHumiAutoOption().getHighDevices(),
                    homeOption.getHumiAutoOption().getLowDevices(),
                    homePodId);
            processSensorData(sensorDataRequest.getLight(),
                    (Number) homeOption.getLightAutoOption().getGreaterThan(),
                    (Number) homeOption.getLightAutoOption().getLessThan(),
                    homeOption.getLightAutoOption().getHighDevices(),
                    homeOption.getLightAutoOption().getLowDevices(),
                    homePodId);
        }
        sensorDataService.saveSensorData(sensorDataRequest);
    }

    private <T extends Number> void processSensorData(T sensorData, Number highValue, Number lowValue, List<DeviceAuto> highDevices, List<DeviceAuto> lowDevices, String topic) throws JsonProcessingException {
        if (sensorData == null) {
            return;
        }

        if (highValue != null && sensorData.doubleValue() > highValue.doubleValue()) {
            processDevices(highDevices, topic);
        }

        if (lowValue != null && sensorData.doubleValue() < lowValue.doubleValue()) {
            processDevices(lowDevices, topic);
        }
    }

    private void processDevices(List<DeviceAuto> devices, String topic) throws JsonProcessingException {
        if (devices != null) {
            Map<String, Integer> deviceStatus = devices.stream()
                    .collect(Collectors.toMap(
                            deviceAuto -> deviceAuto.getName().toLowerCase().replace(" ", "_"),
                            deviceAuto -> "ON".equalsIgnoreCase(deviceAuto.getAction()) ? 1 : 0
                    ));
            publish(topic, objectMapper.writeValueAsString(deviceStatus));
        }
    }

    //  format "homePod/{homePodId}/deviceControl"
    public void publish(String topic, String message) {
        String formattedTopic = formatDeviceControlTopic(topic);
        client.publishWith()
                .topic(formattedTopic)
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



    public String formatDeviceControlTopic(String homePodId) {
        return String.format("homePod/%s/deviceControl", homePodId);
    }

}
