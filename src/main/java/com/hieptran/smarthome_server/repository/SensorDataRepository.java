package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorDataRepository extends MongoRepository<SensorData, String> {
    SensorData findFirstByOrderByIdDesc();
}
