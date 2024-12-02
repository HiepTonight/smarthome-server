//package com.hieptran.smarthome_server;
//
//import com.hieptran.smarthome_server.dto.ApiResponse;
//import com.hieptran.smarthome_server.dto.StatusCodeEnum;
//import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
//import com.hieptran.smarthome_server.dto.responses.SensorDataSummaryResponse;
//import com.hieptran.smarthome_server.model.SensorData;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//public class SensorDataService {
//    public ResponseEntity<ApiResponse<SensorDataSummaryResponse>> getSensorDataSummary() {
//        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
//        LocalDateTime startOfYesterday = startOfToday.minusDays(1);
//        LocalDateTime sevenDaysAgo = startOfToday.minusDays(7);
//
//        List<SensorData> todayData = sensorDataRepository.findByCreatedAtBetween(startOfToday, LocalDateTime.now());
//        List<SensorData> yesterdayData = sensorDataRepository.findByCreatedAtBetween(startOfYesterday, startOfToday);
//        List<SensorData> last7DaysData = sensorDataRepository.findByCreatedAtBetween(sevenDaysAgo, LocalDateTime.now());
//
//        Map<String, Map<String, Float>> tempData = Map.of(
//                "Yesterday", groupDataByInterval(yesterdayData, 3, SensorData::getTemp),
//                "Today", groupDataByInterval(todayData, 3, SensorData::getTemp, startOfToday, LocalDateTime.now()),
//                "Last 7 days", groupDataByDay(last7DaysData, SensorData::getTemp)
//        );
//
//        Map<String, Map<String, Float>> humiData = Map.of(
//                "Yesterday", groupDataByInterval(yesterdayData, 3, SensorData::getHumi),
//                "Today", groupDataByInterval(todayData, 3, SensorData::getHumi, startOfToday, LocalDateTime.now()),
//                "Last 7 days", groupDataByDay(last7DaysData, SensorData::getHumi)
//        );
//
//        Map<String, Map<String, Float>> lightData = Map.of(
//                "Yesterday", groupDataByInterval(yesterdayData, 3, SensorData::getLight),
//                "Today", groupDataByInterval(todayData, 3, SensorData::getLight, startOfToday, LocalDateTime.now()),
//                "Last 7 days", groupDataByDay(last7DaysData, SensorData::getLight)
//        );
//
//        SensorDataSummaryResponse response = SensorDataSummaryResponse.builder()
//                .Temp(tempData)
//                .Humi(humiData)
//                .Light(lightData)
//                .build();
//
//        return ResponseBuilder.successResponse("Get sensor data summary successful", response, StatusCodeEnum.SENSOR0200);
//    }
//
//    private Map<String, Float> groupDataByInterval(List<SensorData> data, int hours, ToFloatFunction<SensorData> mapper) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        return data.stream()
//                .collect(Collectors.groupingBy(sensorData ->
//                        sensorData
//                                .getCreatedAt()
//                                .truncatedTo(ChronoUnit.HOURS)
//                                .withHour(sensorData.getCreatedAt().getHour() / hours * hours)
//                                .format(formatter)))
//                .entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (float) entry.getValue().stream().mapToDouble(dataItem -> mapper.applyAsFloat(dataItem)).average().orElse(0)));
//    }
//
//    private Map<String, Float> groupDataByInterval(List<SensorData> data, int hours, ToFloatFunction<SensorData> mapper, LocalDateTime start, LocalDateTime end) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        Map<String, Float> groupedData = data.stream()
//                .collect(Collectors.groupingBy(sensorData ->
//                        sensorData
//                                .getCreatedAt()
//                                .truncatedTo(ChronoUnit.HOURS)
//                                .withHour(sensorData.getCreatedAt().getHour() / hours * hours)
//                                .format(formatter)))
//                .entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (float) entry.getValue().stream().mapToDouble(dataItem -> mapper.applyAsFloat(dataItem)).average().orElse(0)));
//
//        IntStream.range(0, (int) ChronoUnit.HOURS.between(start, end) / hours + 1)
//                .mapToObj(i -> start.plusHours(i * hours).format(formatter))
//                .forEach(hour -> groupedData.putIfAbsent(hour, 0f));
//
//        return groupedData;
//    }
//
//    private Map<String, Float> groupDataByDay(List<SensorData> data, ToFloatFunction<SensorData> mapper) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");
//        return data.stream()
//                .collect(Collectors.groupingBy(sensorData -> sensorData.getCreatedAt().toLocalDate().format(formatter)))
//                .entrySet().stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (float) entry.getValue().stream().mapToDouble(dataItem -> mapper.applyAsFloat(dataItem)).average().orElse(0)));
//    }
//
//    @FunctionalInterface
//    private interface ToFloatFunction<T> {
//        float applyAsFloat(T value);
//    }
//}
