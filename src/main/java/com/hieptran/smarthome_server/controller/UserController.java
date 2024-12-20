package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.UserService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.AuthenticationRequest;
import com.hieptran.smarthome_server.dto.requests.IntrospectRequest;
import com.hieptran.smarthome_server.dto.requests.UserRequest;
import com.hieptran.smarthome_server.dto.responses.UserLoginResponse;
import com.hieptran.smarthome_server.dto.responses.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return userService.login(authenticationRequest);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo() {
        return userService.getUserInfo();
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<Boolean>> introspect(HttpServletRequest request) {
        return userService.introspectToken(request);
    }

}
