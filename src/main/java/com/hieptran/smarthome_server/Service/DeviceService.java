package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.DeviceRequest;
import com.hieptran.smarthome_server.dto.requests.SettingRequest;
import com.hieptran.smarthome_server.dto.requests.TempAutoRequest;
import com.hieptran.smarthome_server.dto.responses.DeviceResponse;
import com.hieptran.smarthome_server.model.Device;
import com.hieptran.smarthome_server.model.Home;
import com.hieptran.smarthome_server.model.User;
import com.hieptran.smarthome_server.repository.DeviceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    private final MqttService mqttService;

    private final UserService userService;

    @Value("${mqtt.topic.homepod}")
    private String topic;

    public ResponseEntity<ApiResponse<DeviceResponse>> createDevice(DeviceRequest deviceRequest, String homeId) {
        Device device = Device.builder()
                .name(deviceRequest.getName())
                .homeId(homeId)
                .description(deviceRequest.getDescription())
                .status(0)
                .icon(deviceRequest.getIcon())
                .build();

        try {
            deviceRepository.save(device);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("Failed to create Device", StatusCodeEnum.EXCEPTION);
        }

        DeviceResponse deviceResponse = DeviceResponse.fromDevice(device);

        return ResponseBuilder.successResponse("Device created", deviceResponse, StatusCodeEnum.DEVICE0200);
    }

    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getAllDevicesWithHomeId(String homeId) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.DEVICE0200);
            }

            List<DeviceResponse> deviceResponses = deviceRepository.findAllByHomeId(homeId).stream()
                    .map(DeviceResponse::fromDevice)
                    .toList();
            if (deviceResponses.isEmpty()) {
                return ResponseBuilder.badRequestResponse("No devices found", StatusCodeEnum.DEVICE0300);
            }

//            System.out.println(getNameAndStatus());

            return ResponseBuilder.successResponse("Devices found", deviceResponses, StatusCodeEnum.DEVICE0200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("Failed to get devices", StatusCodeEnum.EXCEPTION);
        }
    }

    public ResponseEntity<ApiResponse<DeviceResponse>> triggerDevice(String deviceId, String homePodId) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.DEVICE0300);
            }

            Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
            if (optionalDevice.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Device not found", StatusCodeEnum.DEVICE0300);
            }

            Device device = optionalDevice.get();

            device.setStatus(device.getStatus() == 0 ? 1 : 0);

            deviceRepository.save(device);

            mqttService.publish(homePodId, getNameAndStatus(device.getHomeId()));

            DeviceResponse response = DeviceResponse.fromDevice(device);
            return ResponseBuilder.successResponse("Device triggered", response, StatusCodeEnum.DEVICE0300);

        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("An error occurred: " + e.getMessage(), StatusCodeEnum.EXCEPTION);
        }
    }

    public ResponseEntity<ApiResponse<Objects>> deleteDevice(String deviceId) {
        try {
            User user = userService.getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.DEVICE0200);
            }

            Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
            if (optionalDevice.isEmpty()) {
                return ResponseBuilder.badRequestResponse("Failed to find device", StatusCodeEnum.EXCEPTION);
            }

            try {
                deviceRepository.deleteById(deviceId);
            } catch (Exception e) {
                return ResponseBuilder.badRequestResponse("Failed to delete device", StatusCodeEnum.EXCEPTION);
            }

            return ResponseBuilder.successResponse("Device deleted", StatusCodeEnum.DEVICE0200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("Delete device failed: " + e.getMessage(), StatusCodeEnum.EXCEPTION);
        }
    }

//    public void applyDeviceSetting(SettingRequest settingRequest) {
//        try {
//            Optional<Home> home = deviceRepository.findById(settingRequest.getHomeId());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to apply device setting");
//        }
//    }

    private <T> void processSetting(T setting ) {

    }

    private String getNameAndStatus(String homeId) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Device> devices = deviceRepository.findAllByHomeId(homeId);
            if (devices.isEmpty()) {
                throw new RuntimeException();
            }


            Map<String, Integer> deviceNamesAndStatuses = devices.stream()
                    .collect(Collectors.toMap(
                            device -> device.getName().toLowerCase().replace(" ", "_"),
                            Device::getStatus
                    ));

            return objectMapper.writeValueAsString(deviceNamesAndStatuses);
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
