package com.hieptran.smarthome_server.dto.responses;

import com.hieptran.smarthome_server.model.HomeOption;
import com.hieptran.smarthome_server.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponse {
    private String id;

    private String description;

    private String title;

    private User ownerId;

    private String homePodId;

    private HomeOption homeOption;

    public static HomeResponse from(com.hieptran.smarthome_server.model.Home home) {
        return HomeResponse.builder()
                .id(home.getId().toHexString())
                .description(home.getDescription())
                .title(home.getTitle())
                .ownerId(home.getOwnerId())
                .homePodId(home.getHomePodId())
                .homeOption(home.getHomeOption())
                .build();
    }
}
