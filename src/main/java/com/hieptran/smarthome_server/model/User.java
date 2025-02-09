package com.hieptran.smarthome_server.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private ObjectId id;

    @Field("createdAt")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updatedAt")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("email")
    private String email;

    @Field("phone")
    private String phone;

    @Field("displayName")
    private String displayName;

    @Field("about")
    private String about;

    @Field("avatar")
    private String avatar;

    @Field("githubId")
    private String githubId;

    @Field("googleId")
    private String googleId;

    @Field("oauthToken")
    private List<String> oauthToken;

    @Field("role")
    private String role;

    @Field("isActivated")
    private boolean isActivated;

    @Field("defaultHomeId")
    private String defaultHomeId;

//    @Field("verifyToken")
//    private String verifyToken;
}
