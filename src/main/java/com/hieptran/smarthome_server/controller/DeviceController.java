package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.DeviceService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.DeviceRequest;
import com.hieptran.smarthome_server.dto.responses.DeviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getDevices() {
        return deviceService.getDevices();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(@RequestBody DeviceRequest deviceRequest) {
        return deviceService.createDevice(deviceRequest);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> triggerDevice(@PathVariable("id") String id) {
        return deviceService.triggerDevice(id);
    }
}
