package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthTokenRequest {
    private String name;

    private String accessToken;

    private String refreshToken;
}
