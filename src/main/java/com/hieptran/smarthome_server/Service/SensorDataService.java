package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.config.CacheConfig;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.SensorDataRequest;
import com.hieptran.smarthome_server.dto.responses.SensorDataResponse;
import com.hieptran.smarthome_server.model.DeviceAuto;
import com.hieptran.smarthome_server.model.SensorData;
import com.hieptran.smarthome_server.model.enumeration.SettingKey;
import com.hieptran.smarthome_server.repository.SensorDataRepository;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final ObjectMapper objectMapper;

    private final SensorDataRepository sensorDataRepository;

    private final CacheConfig cache;

    @Value("${mqtt.topic.homepod}")
    private String topic;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveSensorData(SensorDataRequest sensorDataRequest) {
        SensorData sensorData = SensorData.builder()
                .temp(sensorDataRequest.getTemp())
                .humi(sensorDataRequest.getHumi())
                .light(sensorDataRequest.getLight())
                .homePodId(sensorDataRequest.getHomePodId())
                .build();
        try {
            sensorDataRepository.save(sensorData);
        } catch (Exception e) {
            throw new RuntimeException("Save sensor data failed");
        }
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getAllSensorDataWithHomePodId(String homePodId) {
        List<SensorDataResponse> sensorDataResponses = sensorDataRepository.findAllByHomePodId(homePodId)
                .stream()
                .map(SensorDataResponse::fromSensorData)
                .toList();
        return ResponseBuilder.successResponse("Get sensor data successfull", sensorDataResponses, StatusCodeEnum.SENSOR0200);
    }

    public ResponseEntity<ApiResponse<SensorDataResponse>> getLatestSensorData(String homePodId) {
        SensorData sensorData = sensorDataRepository.findFirstByHomePodIdOrderByCreatedAtDesc(homePodId);
        if (sensorData == null) {
            return ResponseBuilder.badRequestResponse("No sensor data available", StatusCodeEnum.SENSOR0200);
        }
        SensorDataResponse sensorDataResponse = SensorDataResponse.fromSensorData(sensorData);
        return ResponseBuilder.successResponse("Get latest sensor data successfull", sensorDataResponse, StatusCodeEnum.SENSOR0200);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getTodaySensorData(String homePodId) {
        LocalDate date = LocalDate.now();
        return getSensorDataAtIntervals(date, homePodId);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getYesterdaySensorData(String homePodId) {
        LocalDate date = LocalDate.now().minusDays(1);
        return getSensorDataAtIntervals(date, homePodId);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getLast7DaysSensorData(String homePodId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();
        return getSensorDataGroupedByDay(sevenDaysAgo, now, homePodId);
    }

//    public void handleCameraRequest

    private ResponseEntity<ApiResponse<List<SensorDataResponse>>> getSensorDataGroupedByDay(LocalDateTime from, LocalDateTime to, String homePodId) {
        List<SensorData> sensorDataList = sensorDataRepository.findByCreatedAtBetweenAndHomePodId(from, to, homePodId);

        Map<LocalDate, List<SensorData>> groupedByDay = sensorDataList.stream()
                .collect(Collectors.groupingBy(sensorData -> sensorData.getCreatedAt().toLocalDate()));

        List<LocalDate> allDates = from.toLocalDate().datesUntil(to.toLocalDate().plusDays(1)).collect(Collectors.toList());

        List<SensorDataResponse> sensorDataResponses = allDates.stream()
                .map(date -> {
                    List<SensorData> dailyData = groupedByDay.getOrDefault(date, List.of());

                    double avgTemp = dailyData.stream().mapToDouble(SensorData::getTemp).average().orElse(0);
                    double avgHumi = dailyData.stream().mapToDouble(SensorData::getHumi).average().orElse(0);
                    double avgLight = dailyData.stream().mapToDouble(SensorData::getLight).average().orElse(0);

                    return SensorDataResponse.builder()
                            .temp((float) avgTemp)
                            .light((float) avgLight)
                            .humi((float) avgHumi)
                            .createdAt(date.atStartOfDay().format(formatter))
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseBuilder.successResponse("Get sensor data successful", sensorDataResponses, StatusCodeEnum.SENSOR0200);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getSensorDataAtIntervals(LocalDate date, String homePodId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> intervals = IntStream.range(0, 8)
                .mapToObj(i -> startOfDay.plusHours(i * 3))
                .filter(interval -> interval.isBefore(now))
                .toList();

        List<SensorData> sensorDataList = sensorDataRepository.findByCreatedAtBetweenAndHomePodId(startOfDay, endOfDay, homePodId);

        List<SensorDataResponse> sensorDataResponses = intervals.stream()
                .map(interval -> {
                    SensorData closestData = sensorDataList.stream()
                            .filter(data -> Math.abs(ChronoUnit.MINUTES.between(data.getCreatedAt(), interval)) <= 30)
                            .min(Comparator.comparingLong(data -> Math.abs(ChronoUnit.MINUTES.between(data.getCreatedAt(), interval))))
                            .orElse(null);

                    if (closestData != null) {
                        return SensorDataResponse.builder()
                                .temp(closestData.getTemp())
                                .light(closestData.getLight())
                                .humi(closestData.getHumi())
                                .createdAt(closestData.getCreatedAt().format(formatter))
                                .build();
                    } else {
                        return SensorDataResponse.builder()
                                .temp(0.0f)
                                .light(0.0f)
                                .humi(0.0f)
                                .createdAt(interval.format(formatter))
                                .build();
                    }
                })
                .collect(Collectors.toList());

        SensorData currentData = sensorDataList.stream()
                .filter(data -> Math.abs(ChronoUnit.MINUTES.between(data.getCreatedAt(), now)) <= 30)
                .min(Comparator.comparingLong(data -> Math.abs(ChronoUnit.MINUTES.between(data.getCreatedAt(), now))))
                .orElse(null);

        if (currentData != null) {
            sensorDataResponses.add(SensorDataResponse.builder()
                    .temp(currentData.getTemp())
                    .light(currentData.getLight())
                    .humi(currentData.getHumi())
                    .createdAt(currentData.getCreatedAt().format(formatter))
                    .build());
        }

        return ResponseBuilder.successResponse("Get sensor data successful", sensorDataResponses, StatusCodeEnum.SENSOR0200);
    }

//    private <T extends Number> void processSensorData(T sensorData, SettingKey highKey, SettingKey lowKey, SettingKey highDevicesKey, SettingKey lowDevicesKey) throws JsonProcessingException {
//        if (sensorData == null) {
//            return;
//        }
//
//        Number highValue = (Number) cache.get(highKey.toString());
//        Number lowValue = (Number) cache.get(lowKey.toString());
//
//        if (highValue != null && sensorData.doubleValue() > highValue.doubleValue()) {
//            processDevices(highDevicesKey);
//        }
//
//        if (lowValue != null && sensorData.doubleValue() < lowValue.doubleValue()) {
//            processDevices(lowDevicesKey);
//        }
//    }
//
//    private void processDevices(SettingKey devicesKey) throws JsonProcessingException {
//        List<DeviceAuto> devices = (List<DeviceAuto>) cache.get(devicesKey.toString());
//        if (devices != null) {
//            Map<String, Integer> deviceStatus = devices.stream()
//                    .collect(Collectors.toMap(
//                            deviceAuto -> deviceAuto.getName().toLowerCase().replace(" ", "_"),
//                            deviceAuto -> "ON".equalsIgnoreCase(deviceAuto.getAction()) ? 1 : 0
//                    ));
//            publish(objectMapper.writeValueAsString(deviceStatus));
//        }
//    }
}
