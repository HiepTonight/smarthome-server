package com.hieptran.smarthome_server.dto.requests;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String username;

    private String password;

    private String email;

    private String displayName;
}
