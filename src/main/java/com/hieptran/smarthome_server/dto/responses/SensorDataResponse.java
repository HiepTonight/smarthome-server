package com.hieptran.smarthome_server.dto.responses;

import com.hieptran.smarthome_server.model.SensorData;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDataResponse {
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private float temp;

    private float humi;

    private float light;

    public static SensorDataResponse fromSensorData(SensorData sensorData) {
        return SensorDataResponse.builder()
                .createdAt(sensorData.getCreatedAt())
                .updatedAt(sensorData.getUpdatedAt())
                .temp(sensorData.getTemp())
                .humi(sensorData.getHumi())
                .light(sensorData.getLight())
                .build();
    }
}
