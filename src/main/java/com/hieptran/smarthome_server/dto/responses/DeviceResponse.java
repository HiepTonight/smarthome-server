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

    private String id;

    private String name;

    private String description;

    private String homePodId;

    private int status;

    private String icon;

    public static DeviceResponse fromDevice(Device device) {
        return DeviceResponse.builder()
                .id(device.getId().toHexString())
                .name(device.getName())
                .description(device.getDescription())
                .homePodId(device.getHomePodId())
                .status(device.getStatus())
                .icon(device.getIcon())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }
}
