package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.Home;
import com.hieptran.smarthome_server.model.HomeOption;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface HomeRepository extends MongoRepository<Home, String> {
    Home findByHomePodId(String homePodId) throws Exception;

//    List<Map<String, HomeOption>> findAllHomeOptions() throws Exception;
}
