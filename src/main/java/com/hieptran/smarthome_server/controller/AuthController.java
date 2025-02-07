package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.AuthService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.responses.UserLoginResponse;
import com.hieptran.smarthome_server.dto.responses.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<UserLoginResponse>> handleGoogleCallback(
            @RequestParam String code,
//            @RequestParam String state,
            HttpServletResponse response,
            HttpSession session
    ) {
        return authService.handleGoogleCallback(code, session, response);
    }

    @GetMapping("/redirect")
    public ResponseEntity<?> handleRedirect(
           HttpServletResponse response
    ) throws IOException {
        response.sendRedirect("http://localhost:5173");
        return null;
    }
}
