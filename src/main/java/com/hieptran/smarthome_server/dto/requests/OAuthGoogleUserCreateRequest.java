package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthGoogleUserCreateRequest {

    private String displayName;

    private String email;

    private String avatar;

    private String googleId;

}
