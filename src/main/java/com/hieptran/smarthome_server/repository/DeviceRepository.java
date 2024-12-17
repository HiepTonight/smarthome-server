package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeviceRepository extends MongoRepository<Device, String> {
    List<Device> findAllByHomeId(String homeId);
}
