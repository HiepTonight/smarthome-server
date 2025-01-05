package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.DeviceService;
import com.hieptran.smarthome_server.Service.SseService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.DeviceRequest;
import com.hieptran.smarthome_server.dto.responses.DeviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/home/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    private final SseService sseService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getDevices(@RequestParam("id")String homePodId) {
        return deviceService.getAllDevicesWithHomePodId(homePodId);
    }

    @GetMapping("/sse")
    public SseEmitter streamDeviceEvents() throws AccessDeniedException {
        return sseService.addEmitter();
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(@PathVariable("id") String homePodId, @RequestBody DeviceRequest deviceRequest) {
        return deviceService.createDevice(deviceRequest, homePodId);
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
