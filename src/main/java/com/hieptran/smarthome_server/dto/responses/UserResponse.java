package com.hieptran.smarthome_server.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String username;

    private String email;

    private String displayName;
}
}
