package com.hieptran.smarthome_server.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponse {
    private String accessToken;
}
