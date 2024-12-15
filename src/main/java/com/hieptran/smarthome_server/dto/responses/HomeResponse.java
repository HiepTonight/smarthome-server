package com.hieptran.smarthome_server.dto.responses;

import com.hieptran.smarthome_server.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponse {
    private String description;

    private String title;

    private User ownerId;

    public static HomeResponse from(com.hieptran.smarthome_server.model.Home home) {
        return HomeResponse.builder()
                .description(home.getDescription())
                .title(home.getTitle())
                .ownerId(home.getOwnerId())
                .build();
    }
}
