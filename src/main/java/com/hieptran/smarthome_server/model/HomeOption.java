package com.hieptran.smarthome_server.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document("homeOptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeOption {
//    @Id
//    private ObjectId id;
//
//    @Field("createdAt")
//    @CreatedDate
//    private LocalDateTime createdAt;

    @Field("updatedAt")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("controlType")
    private String controlType;

    @Field("tempAutoOption")
    private TempAutoOption tempAutoOption;

    @Field("humiAutoOption")
    private HumiAutoOption humiAutoOption;

    @Field("lightAutoOption")
    private LightAutoOption lightAutoOption;
}
