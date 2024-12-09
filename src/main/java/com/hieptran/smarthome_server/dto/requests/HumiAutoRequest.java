package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumiAutoRequest {
    private float greaterThanHumi;

    private float lessThanHumi;
}
