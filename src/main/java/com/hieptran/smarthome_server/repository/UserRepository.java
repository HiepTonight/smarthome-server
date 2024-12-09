package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByUsernameOrEmail(String username, String email);

    List<User> findByIdIn(List<String> userIds);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
