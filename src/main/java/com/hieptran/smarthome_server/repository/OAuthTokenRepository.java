package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.OauthToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthTokenRepository extends MongoRepository<OauthToken, String> {

}
