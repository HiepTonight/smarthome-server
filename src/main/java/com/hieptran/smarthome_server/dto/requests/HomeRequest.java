package com.hieptran.smarthome_server.dto.requests;

import com.hieptran.smarthome_server.model.User;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeRequest {

    private String description;

    private String title;

    private String ownerId;
}
