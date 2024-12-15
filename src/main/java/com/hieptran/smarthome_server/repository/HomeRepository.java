package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.Home;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HomeRepository extends MongoRepository<Home, String> {

}
