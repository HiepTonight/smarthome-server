package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.SensorDataRequest;
import com.hieptran.smarthome_server.dto.responses.SensorDataResponse;
import com.hieptran.smarthome_server.model.SensorData;
import com.hieptran.smarthome_server.repository.SensorDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
