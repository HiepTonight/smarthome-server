package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<Device, String> {
}
