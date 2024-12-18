package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.DeviceService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.DeviceRequest;
import com.hieptran.smarthome_server.dto.responses.DeviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/home/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getDevices(@RequestParam("id")String homeId) {
        return deviceService.getAllDevicesWithHomeId(homeId);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(@PathVariable("id") String homeId, @RequestBody DeviceRequest deviceRequest) {
        return deviceService.createDevice(deviceRequest, homeId);
    }

    @PostMapping("/{id}/trigger")
    public ResponseEntity<ApiResponse<DeviceResponse>> triggerDevice(@PathVariable("id") String id, @RequestParam("homePodId") String homePodId) {
        return deviceService.triggerDevice(id, homePodId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Objects>> deleteDevice(@PathVariable("id") String id) {
        return deviceService.deleteDevice(id);
    }
}
