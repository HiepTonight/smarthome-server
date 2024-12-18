package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.SensorDataService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.responses.SensorDataResponse;
import com.hieptran.smarthome_server.dto.responses.SensorDataSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/sensor")
public class SensorDataController {
    private final SensorDataService sensorDataService;

    @GetMapping("/{id}/data/all")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getAllSensorData(@PathVariable("id") String homePodId) {
        return sensorDataService.getAllSensorDataWithHomePodId(homePodId);
    }

    @GetMapping("/{id}/data/yesterday")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getYesterdayData(@PathVariable("id") String homePodId) {
        return sensorDataService.getYesterdaySensorData(homePodId);
    }

    @GetMapping("/{id}/data/today")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getTodayData(@PathVariable("id") String homePodId) {
        return sensorDataService.getTodaySensorData(homePodId);
    }

    @GetMapping("/{id}/data/last7days")
    public ResponseEntity<ApiResponse<List<SensorDataResponse>>> getLast7DaysSensorData(@PathVariable("id") String homePodId) {
        return sensorDataService.getLast7DaysSensorData(homePodId);
    }

    @GetMapping("/{id}/latest")
    public ResponseEntity<ApiResponse<SensorDataResponse>> getLatestSensorData(@PathVariable("id") String homePodId) {
        return sensorDataService.getLatestSensorData(homePodId);
    }

}
