package com.hieptran.smarthome_server.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("oauthTokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OauthToken {
    @Id
    private ObjectId id;

    @Field("name")
    private String name;

    @Field("accessToken")
    private String accessToken;

    @Field("refreshToken")
    private String refreshToken;
}
