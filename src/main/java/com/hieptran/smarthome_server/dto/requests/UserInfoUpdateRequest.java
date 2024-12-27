package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoUpdateRequest {
    private String username;

    private String email;

    private String displayName;

    private String phone;

    private String about;

    private String homeDefaultId;
}
