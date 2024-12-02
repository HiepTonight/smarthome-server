package com.hieptran.smarthome_server.dto.responses;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDataSummaryResponse {
    private String status;
    private List<Sensor> data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Sensor {
        private String type;
        private Map<String, List<TimeValuePair>> values;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeValuePair {
        private String time;
        private float value;
    }
}