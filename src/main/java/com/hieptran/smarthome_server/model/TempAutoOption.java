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
@Document("tempAutoOptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempAutoOption {
    @Id
    private ObjectId id;

    @Field("createdAt")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updatedAt")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("homeOptionId")
    private HomeOption homeOptionId;

    @Field("greaterThanTemp")
    private float greaterThanTemp;

    @Field("lessThanTemp")
    private float lessThanTemp;

    @Field("DeviceTrigger")
    private List<Device> devices;
}
