package com.hieptran.smarthome_server.repository;

import com.hieptran.smarthome_server.model.VerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends MongoRepository<VerificationCode, String> {
   Optional<VerificationCode> findByEmailAndCode(String email, String code);
   List<VerificationCode> findByEmail(String email);
   void deleteAllByExpiredAtBefore(LocalDateTime dateTime);
}
