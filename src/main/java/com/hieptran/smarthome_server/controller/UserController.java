package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.UserService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.*;
import com.hieptran.smarthome_server.dto.responses.AccessTokenResponse;
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

    @PostMapping("/update-info")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserInfoUpdateRequest userInfoUpdateRequest) {
        return userService.updateUser(userInfoUpdateRequest);
    }

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<UserResponse>> updatePassword(@RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        return userService.updateUserPassword(userPasswordUpdateRequest);
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

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return userService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/update-homeDefault")
    private ResponseEntity<ApiResponse<UserResponse>> updateHomeDefault(@RequestBody HomeDefaultRequest userHomeDefaultRequest) {
        return userService.setHomeDefault(userHomeDefaultRequest);
    }

}
