package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.HomeOption;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HomeOptionRepository extends MongoRepository<HomeOption, String> {
}
