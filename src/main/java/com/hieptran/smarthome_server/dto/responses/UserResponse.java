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

    private String phone;

    private String displayName;

    private String avatar;

    private String about;

    private String role;

    private boolean isActivated;

    private String defaultHomeId;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .about(user.getAbout())
                .role(user.getRole())
                .isActivated(user.isActivated())
                .defaultHomeId(user.getDefaultHomeId())
                .build();
    }

}
