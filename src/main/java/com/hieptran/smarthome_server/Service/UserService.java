package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.UserRequest;
import com.hieptran.smarthome_server.model.User;
import com.hieptran.smarthome_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponse<User>> createUser(UserRequest userRequest) {
        try {
            if (userRepository.existsByUsername(userRequest.getUsername())) {
                return ResponseBuilder.badRequestResponse("Username is already taken", StatusCodeEnum.EXCEPTION);
            }

            if (userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseBuilder.badRequestResponse("Email is already taken", StatusCodeEnum.EXCEPTION);
            }

            User user = User.builder()
                    .username(userRequest.getUsername())
                    .password(userRequest.getPassword())
                    .email(userRequest.getEmail())
                    .displayName(userRequest.getDisplayName())
                    .role("client")
                    .isActivated(true)
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .build();

            return ResponseBuilder.successResponse("User created", userRepository.save(user), StatusCodeEnum.LOGIN0201);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse("Failed to create User", StatusCodeEnum.EXCEPTION);
        }
    }
}
