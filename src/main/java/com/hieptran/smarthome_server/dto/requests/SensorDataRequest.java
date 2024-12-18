package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDataRequest {

    private String homePodId;

    private float temp;

    private float humi;

    private float light;
}
