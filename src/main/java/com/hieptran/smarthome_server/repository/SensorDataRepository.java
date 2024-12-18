package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepository extends MongoRepository<SensorData, String> {
    List<SensorData> findAllByHomePodId(String homePodId);

    SensorData findFirstByHomePodIdOrderByCreatedAtDesc(String homePodId);

    SensorData findFirstByOrderByIdDesc();

    List<SensorData> findByCreatedAtAfter(LocalDateTime timestamp);

    List<SensorData> findByCreatedAtBetweenAndHomePodId(LocalDateTime from, LocalDateTime to, String homePodId);

    List<SensorData> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

}
