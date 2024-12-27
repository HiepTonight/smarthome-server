package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPasswordUpdateRequest {
    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}
