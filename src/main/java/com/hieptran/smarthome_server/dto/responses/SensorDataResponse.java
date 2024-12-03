package com.hieptran.smarthome_server.dto.responses;

import com.hieptran.smarthome_server.model.SensorData;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDataResponse {
    private String  createdAt;

    private String updatedAt;

    private float temp;

    private float humi;

    private float light;

    public static SensorDataResponse fromSensorData(SensorData sensorData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return SensorDataResponse.builder()
                .createdAt(sensorData.getCreatedAt().format(formatter))
                .updatedAt(sensorData.getUpdatedAt().format(formatter))
                .temp(sensorData.getTemp())
                .humi(sensorData.getHumi())
                .light(sensorData.getLight())
                .build();
    }
}
