package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightAutoRequest {
    private Float high;

    private Float low;

    private List<DeviceAutoRequest> highDevices;

    private List<DeviceAutoRequest> lowDevices;
}
