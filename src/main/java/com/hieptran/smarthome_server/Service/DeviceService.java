package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.DeviceRequest;
import com.hieptran.smarthome_server.dto.responses.DeviceResponse;
import com.hieptran.smarthome_server.model.Device;
import com.hieptran.smarthome_server.repository.DeviceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(DeviceRequest deviceRequest) {
        Device device = Device.builder()
                .name(deviceRequest.getName())
                .description(deviceRequest.getDescription())
                .status(0)
                .build();

        try {
            deviceRepository.save(device);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("Failed to create Device", StatusCodeEnum.EXCEPTION);
        }

        DeviceResponse deviceResponse = DeviceResponse.fromDevice(device);

        return ResponseBuilder.successResponse("Device created", deviceResponse, StatusCodeEnum.DEVICE0200);
    }

    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getDevices() {
        List<DeviceResponse> devices;

        try {
            devices = deviceRepository.findAll()
                    .stream()
                    .map(DeviceResponse::fromDevice)
                    .toList();
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("Failed to get all devices", StatusCodeEnum.EXCEPTION);
        }

        return ResponseBuilder.successResponse("Devices data", devices, StatusCodeEnum.DEVICE0200);
    }

    public ResponseEntity<ApiResponse<DeviceResponse>> triggerDevice(String deviceId) {
        try {
            Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
            if (optionalDevice.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Device not found", StatusCodeEnum.DEVICE0300);
            }

            Device device = optionalDevice.get();

            device.setStatus(device.getStatus() == 0 ? 1 : 0);

            deviceRepository.save(device);

            //Mqtt service

            DeviceResponse response = DeviceResponse.fromDevice(device);
            return ResponseBuilder.successResponse("Device triggered", response, StatusCodeEnum.DEVICE0300);

        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("An error occurred: " + e.getMessage(), StatusCodeEnum.EXCEPTION);
        }
    }

}
