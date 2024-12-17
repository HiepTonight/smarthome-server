package com.hieptran.smarthome_server.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceAuto {
    @Id
    private String id;

//    @Field("createdAt")
//    @CreatedDate
//    private LocalDateTime createdAt;
//
//    @Field("updatedAt")
//    @LastModifiedDate
//    private LocalDateTime updatedAt;

    @Field("name")
    private String name;

    @Field("enabled")
    private Boolean enabled;

    @Field("action")
    private String action;
}
