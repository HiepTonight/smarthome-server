package com.hieptran.smarthome_server.dto.responses;

import com.hieptran.smarthome_server.model.User;
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

    private String role;

    private boolean isActivated;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .isActivated(user.isActivated())
                .build();
    }

}
