package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempAutoRequest {
    private float greaterThanTemp;

    private float lessThanTemp;
}
