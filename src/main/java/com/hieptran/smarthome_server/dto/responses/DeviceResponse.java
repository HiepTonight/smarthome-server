package com.hieptran.smarthome_server.dto.responses;

import com.hieptran.smarthome_server.model.Device;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String name;

    private String description;

    private int status;

    public static DeviceResponse fromDevice(Device device) {
        return DeviceResponse.builder()
                .name(device.getName())
                .description(device.getDescription())
                .status(device.getStatus())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }
}
