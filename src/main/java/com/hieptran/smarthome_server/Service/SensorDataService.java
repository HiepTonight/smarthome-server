package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.SensorDataRequest;
import com.hieptran.smarthome_server.dto.responses.SensorDataResponse;
import com.hieptran.smarthome_server.dto.responses.SensorDataSummaryResponse;
import com.hieptran.smarthome_server.model.SensorData;
import com.hieptran.smarthome_server.repository.SensorDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;

    public void saveSensorData(SensorDataRequest sensorDataRequest) {
        SensorData sensorData = SensorData.builder()
                .temp(sensorDataRequest.getTemp())
                .humi(sensorDataRequest.getHumi())
                .light(sensorDataRequest.getLight())
                .build();
        try {
            sensorDataRepository.save(sensorData);
        } catch (Exception e) {
            throw new RuntimeException("Save sensor data failed");
        }
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getAllSensorData() {
        List<SensorDataResponse> sensorDataResponses = sensorDataRepository.findAll()
                .stream()
                .map(SensorDataResponse::fromSensorData)
                .toList();
        return ResponseBuilder.successResponse("Get sensor data successfull", sensorDataResponses, StatusCodeEnum.SENSOR0200);
    }

    public ResponseEntity<ApiResponse<SensorDataResponse>> getLatestSensorData() {
        SensorData sensorData = sensorDataRepository.findFirstByOrderByIdDesc();
        if (sensorData == null) {
            return ResponseBuilder.badRequestResponse("No sensor data available", StatusCodeEnum.SENSOR0200);
        }
        SensorDataResponse sensorDataResponse = SensorDataResponse.fromSensorData(sensorData);
        return ResponseBuilder.successResponse("Get latest sensor data successfull", sensorDataResponse, StatusCodeEnum.SENSOR0200);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getTodaySensorData() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return getSensorDataGroupedByInterval(startOfDay, now, 3);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getYesterdaySensorData() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endOfYesterday = LocalDate.now().atStartOfDay();
        return getSensorDataGroupedByInterval(startOfYesterday, endOfYesterday, 3);
    }

    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getLast7DaysSensorData() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();
        return getSensorDataGroupedByDay(sevenDaysAgo, now);
    }

    private ResponseEntity<ApiResponse<List<SensorDataResponse>>> getSensorDataGroupedByDay(LocalDateTime from, LocalDateTime to) {
        List<SensorData> sensorDataList = sensorDataRepository.findByCreatedAtBetween(from, to);

        Map<LocalDate, List<SensorData>> groupedByDay = sensorDataList.stream()
                .collect(Collectors.groupingBy(sensorData -> sensorData.getCreatedAt().toLocalDate()));

        List<SensorDataResponse> sensorDataResponses = groupedByDay.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<SensorData> dailyData = entry.getValue();

                    double avgTemp = dailyData.stream().mapToDouble(SensorData::getTemp).average().orElse(0);
                    double avgHumi = dailyData.stream().mapToDouble(SensorData::getHumi).average().orElse(0);
                    double avgLight = dailyData.stream().mapToDouble(SensorData::getLight).average().orElse(0);

                    return SensorDataResponse.builder()
                            .temp((float) avgTemp)
                            .light((float) avgLight)
                            .humi((float) avgHumi)
                            .createdAt(date.atStartOfDay())
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseBuilder.successResponse("Get sensor data successful", sensorDataResponses, StatusCodeEnum.SENSOR0200);
    }

    private ResponseEntity<ApiResponse<List<SensorDataResponse>>> getSensorDataGroupedByInterval(LocalDateTime from, LocalDateTime to, int hours) {
        List<SensorData> sensorDataList = sensorDataRepository.findByCreatedAtBetween(from, to);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<SensorDataResponse> sensorDataResponses = IntStream.range(0, (int) ChronoUnit.HOURS.between(from, to) / hours + 1)
                .mapToObj(i -> from.plusHours(i * hours))
                .map(targetTime -> {
                    SensorData closestData = sensorDataList.stream()
                            .min((d1, d2) -> Long.compare(
                                    Math.abs(ChronoUnit.SECONDS.between(d1.getCreatedAt(), targetTime)),
                                    Math.abs(ChronoUnit.SECONDS.between(d2.getCreatedAt(), targetTime))
                            ))
                            .orElse(null);

                    if (closestData != null) {
                        return SensorDataResponse.builder()
                                .temp(closestData.getTemp())
                                .light(closestData.getLight())
                                .humi(closestData.getHumi())
                                .createdAt(closestData.getCreatedAt())
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());

        return ResponseBuilder.successResponse("Get sensor data successful", sensorDataResponses, StatusCodeEnum.SENSOR0200);
    }
}
