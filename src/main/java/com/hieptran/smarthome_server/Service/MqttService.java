package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.config.CacheConfig;
import com.hieptran.smarthome_server.dto.EventCodeEnum;
import com.hieptran.smarthome_server.dto.requests.SensorDataRequest;
import com.hieptran.smarthome_server.dto.responses.DeviceResponse;
import com.hieptran.smarthome_server.dto.responses.SensorDataResponse;
import com.hieptran.smarthome_server.model.*;
import com.hieptran.smarthome_server.repository.DeviceRepository;
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
    private final ObjectMapper objectMapper;

    private final Mqtt5AsyncClient client;

    private final SensorDataService sensorDataService;

    private final SseService sseService;

    private final HomeRepository homeRepository;

    private final CacheConfig cache;
    private final DeviceRepository deviceRepository;

    @Value("${mqtt.topic.homepod}")
    private String homePodTopic;

    @Value("${mqtt.topic.device}")
    private String deviceControlTopic;

    @PostConstruct
    public void subscribeAllHomePods() throws Exception {
        List<Home> homePodIds = homeRepository.findAll();
        for (String homePodId : homePodIds.stream().map(Home::getHomePodId).toList()) {
            subcribe(homePodId);
            subcribeFaceTopic(homePodId);
        }
    }

    public void subcribe(String homePodId) {
        String formattedTopic = String.format("homePod/%s", homePodId);
        processSubcribe(formattedTopic);
    }

    public void subcribeFaceTopic(String homePodId) {
        String formattedFaceTopic = String.format("homePod/%s/faceRecognize", homePodId);
        processSubcribe(formattedFaceTopic);
    }

    private void processSubcribe(String formattedFaceTopic) {
        client.subscribeWith()
                .topicFilter(formattedFaceTopic)
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
                        System.err.println("Failed to subscribe to topic: " + formattedFaceTopic);
                    } else {
                        System.out.println("Successfully subscribed to topic: " + formattedFaceTopic);
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
        String homePodId = extractHomePodIdFromTopic(topic, 1);
        if (topic.endsWith("/faceRecognize")) {
            processFaceData(homePodId, payload);
        } else {
            processMessage(homePodId, payload);
        }
    }

    private String extractHomePodIdFromTopic(String topic, int segment) {
        String[] segments = topic.split("/");
        return segments[segment]; //  format "homePod/{homePodId}" or homePod/{homePodId}/face"
    }

    private void processFaceData(String homePodId, byte[] payload) throws IOException {
        String payloadString = new String(payload, StandardCharsets.UTF_8);
        JsonNode jsonNode = objectMapper.readTree(payloadString);

        if (jsonNode.has("face")) {
            int faceValue = jsonNode.get("face").asInt();

            if (faceValue != 1) {
                return;
            }

            String message = String.format("{\"door\": %d}", faceValue);

            Device door = deviceRepository.findByHomePodIdAndName(homePodId, "Door");

            if (door == null) {
                System.out.println("Door not found");
                return;
            }

            door.setStatus(faceValue);
            deviceRepository.save(door);

            publish(homePodId, message);

            sseService.send(
                    homePodId,
                    EventCodeEnum.DEVICE_UPDATE_EVENT,
                    EventCodeEnum.DEVICE_UPDATE_EVENT,
                    List.of(DeviceResponse.fromDevice(door)
                    )
            );

            System.out.println((faceValue == 1 ? "Opening" : "Closing") + " door for homePodId: " + homePodId);
        }
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

        SensorDataResponse sensorDataResponse =sensorDataService.saveSensorData(sensorDataRequest);

        sseService.send(
                homePodId,
                EventCodeEnum.SENSOR_DATA_UPDATE_EVENT,
                EventCodeEnum.SENSOR_DATA_UPDATE_EVENT,
                sensorDataResponse
        );
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
            List<Device> deviceList = deviceRepository.findAllByHomePodId(topic);
            Map<String, Device> deviceMap = deviceList.stream()
                    .collect(Collectors.toMap(device -> device.getId().toString(), device -> device));

            for (DeviceAuto deviceAuto : devices) {
                Device device = deviceMap.get(deviceAuto.getId());
                if (device != null) {
                    device.setStatus("ON".equalsIgnoreCase(deviceAuto.getAction()) ? 1 : 0);
                }
            }

            deviceRepository.saveAll(deviceMap.values());

            Map<String, Integer> deviceStatus = devices.stream()
                    .collect(Collectors.toMap(
                            deviceAuto -> deviceAuto.getName().toLowerCase().replace(" ", "_"),
                            deviceAuto -> "ON".equalsIgnoreCase(deviceAuto.getAction()) ? 1 : 0
                    ));

            publish(topic, objectMapper.writeValueAsString(deviceStatus));

            sseService.send(
                    topic,
                    EventCodeEnum.DEVICE_UPDATE_EVENT,
                    EventCodeEnum.DEVICE_UPDATE_EVENT,
                    deviceMap.values().stream()
                            .map(DeviceResponse::fromDevice)
                            .toList()
            );

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

    //  format "homePod/{homePodId}/deviceControl"
    public void publishFaceRecognize(String topic, String message) {
        String formattedTopic = formatFaceControlTopic(topic);
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

    private String formatDeviceControlTopic(String homePodId) {
        return String.format("homePod/%s/controlDevice", homePodId);
    }

    private String formatFaceControlTopic(String homePodId) {
        return String.format("homePod/%s/doorStatus", homePodId);
    }

//    homePod/sensorData/controlDevice homePod/sensorData {"temp": 22, "humi": 60, "light": 91}

}
