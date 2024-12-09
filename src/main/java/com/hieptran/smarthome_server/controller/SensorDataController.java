package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.SensorDataService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.responses.SensorDataResponse;
import com.hieptran.smarthome_server.dto.responses.SensorDataSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorDataController {
    private final SensorDataService sensorDataService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getAllSensorData() {
        return sensorDataService.getAllSensorData();
    }

    @GetMapping("/data/yesterday")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getYesterdayData() {
        return sensorDataService.getYesterdaySensorData();
    }

    @GetMapping("/data/today")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getTodayData() {
        return sensorDataService.getTodaySensorData();
    }

    @GetMapping("/data/last7days")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getLast7DaysSensorData() {
        return sensorDataService.getLast7DaysSensorData();
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<SensorDataResponse>> getLatestSensorData() {
        return sensorDataService.getLatestSensorData();
    }
}
