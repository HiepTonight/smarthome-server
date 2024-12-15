package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceAutoRequest {
    private String id;

    private String name;

    private Boolean enabled;

    private String action;
}
