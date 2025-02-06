package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    private String username;

    private String email;

    private String password;

    private String confirmPassword;
}
