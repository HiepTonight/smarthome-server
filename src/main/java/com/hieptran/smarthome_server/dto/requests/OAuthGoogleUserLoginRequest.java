package com.hieptran.smarthome_server.dto.requests;

import com.hieptran.smarthome_server.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthGoogleUserLoginRequest {
    private User user;
}
