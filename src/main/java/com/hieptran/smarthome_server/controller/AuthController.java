package com.hieptran.smarthome_server.controller;

import com.hieptran.smarthome_server.Service.AuthService;
import com.hieptran.smarthome_server.Service.EmailService;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.requests.EmailVerificationRequest;
import com.hieptran.smarthome_server.dto.responses.UserLoginResponse;
import com.hieptran.smarthome_server.dto.responses.UserResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<UserLoginResponse>> handleGoogleCallback(
            @RequestParam String code
//            @RequestParam String state,
//            HttpServletResponse response,
//            HttpSession session
    ) {
        return authService.handleGoogleCallback(code);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<UserLoginResponse>> verify(EmailVerificationRequest emailVerificationRequest) {
        return authService.verifyEmail(emailVerificationRequest);
    }

    @GetMapping("/redirect")
    public ResponseEntity<?> handleRedirect(
           HttpServletResponse response
    ) throws IOException {
        response.sendRedirect("http://localhost:5173");
        return null;
    }

}
