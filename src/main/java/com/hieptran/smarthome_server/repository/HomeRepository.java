package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.Home;
import com.hieptran.smarthome_server.model.HomeOption;
import com.hieptran.smarthome_server.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;

public interface HomeRepository extends MongoRepository<Home, String> {
    Home findByHomePodId(String homePodId) throws Exception;

    List<Home> findAllByOwnerId(User ownerId) throws Exception;

    @Query(value = "{}", fields = "{homePodId: 1}")
    List<String> findAllHomePodIds() throws Exception;

//    List<Map<String, HomeOption>> findAllHomeOptions() throws Exception;
}
