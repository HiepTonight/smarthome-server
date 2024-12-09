package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightAutoRequest {
    private float greaterThanLight;

    private float lessThanLight;
}
