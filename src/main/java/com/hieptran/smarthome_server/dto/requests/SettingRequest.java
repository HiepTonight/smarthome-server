package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingRequest {
//    private String homeId;

    private String controlType;

    private TempAutoRequest temperature;

    private HumiAutoRequest humidity;

    private LightAutoRequest light;
}
