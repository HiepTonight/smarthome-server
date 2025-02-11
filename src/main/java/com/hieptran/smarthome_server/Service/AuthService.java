package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.EmailVerificationRequest;
import com.hieptran.smarthome_server.dto.requests.OAuthGoogleUserCreateRequest;
import com.hieptran.smarthome_server.dto.requests.OAuthGoogleUserLoginRequest;
import com.hieptran.smarthome_server.dto.requests.OAuthTokenRequest;
import com.hieptran.smarthome_server.dto.responses.UserLoginResponse;
import com.hieptran.smarthome_server.dto.responses.UserResponse;
import com.hieptran.smarthome_server.model.OauthToken;
import com.hieptran.smarthome_server.model.User;
import com.hieptran.smarthome_server.model.VerificationCode;
import com.hieptran.smarthome_server.repository.UserRepository;
import com.hieptran.smarthome_server.repository.VerificationCodeRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.user-info-uri}")
    private String googleUserInfoUri;

    private final HttpSession session;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final VerificationCodeRepository verificationCodeRepository;

    private final UserService userService;

    private final JwtService jwtService;

    public ResponseEntity<ApiResponse<String>> generateAuthUrl() {
        String state = UUID.randomUUID().toString();
//        session.setAttribute("oauth2_state", state);

        String authorizationUrl = UriComponentsBuilder
                .fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("state", state)
                .queryParam("access_type", "offline")
//                .queryParam("prompt", "consent") // Yêu cầu người dùng cấp quyền mỗi lần đăng nhập
                .build()
                .toUriString();

        return ResponseBuilder.successResponse("Authorization URL generated", authorizationUrl, StatusCodeEnum.USER0200);
    }

    public ResponseEntity<ApiResponse<UserLoginResponse>> handleGoogleCallback(String authorizationCode) {

        String requestBody = UriComponentsBuilder.newInstance()
                .queryParam("code", authorizationCode) // Thêm authorization code
                .queryParam("client_id", googleClientId) // Thêm client_id
                .queryParam("client_secret", googleClientSecret) // Thêm client_secret
                .queryParam("redirect_uri", googleRedirectUri) // Thêm redirect_uri
                .queryParam("grant_type", "authorization_code") // Thêm grant_type
                .build()
                .toString()
                .replace("?", "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            // Gửi request và nhận response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                String responseBody = response.body();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String accessToken = jsonNode.get("access_token").asText();
//                String refreshToken = jsonNode.get("refresh_token").asText();

//              getUserInfoFromGoogle(accessToken);
                HttpRequest userInfoRequest = HttpRequest.newBuilder()
                        .uri(URI.create(googleUserInfoUri))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                try {
                    HttpResponse<String> userInfoResponse = httpClient.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());
                    String googleUserInfo = userInfoResponse.body();
                    JsonNode googleUserInfoNode = objectMapper.readTree(googleUserInfo);

                    User user = userRepository.findByGoogleId(googleUserInfoNode.get("sub").asText());

                    if (user == null) {
                        OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.builder()
                                .name("google")
                                .accessToken(accessToken)
                                .refreshToken(jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : "")
                                .build();

                        OAuthGoogleUserCreateRequest oAuthGoogleUserCreateRequest = OAuthGoogleUserCreateRequest.builder()
                                .displayName(googleUserInfoNode.get("name").asText())
                                .email(googleUserInfoNode.get("email").asText())
                                .avatar(googleUserInfoNode.get("picture").asText())
                                .googleId(googleUserInfoNode.get("sub").asText())
                                .build();

                        return userService.oAuthGoogleUserCreate(oAuthGoogleUserCreateRequest , oAuthTokenRequest);
                    }

                    OAuthGoogleUserLoginRequest oAuthGoogleUserLoginRequest = OAuthGoogleUserLoginRequest.builder()
                            .user(user)
                            .build();

                    return userService.oAuthGoogleUserLogin(oAuthGoogleUserLoginRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseBuilder.badRequestResponse("Failed to get user info from Google", StatusCodeEnum.USER0200);
                }

            } else {
                String errorResponse = response.body();
                return ResponseBuilder.badRequestResponse(errorResponse, StatusCodeEnum.USER0200);
            }

        } catch (Exception e) {
//            e.printStackTrace();
            return ResponseBuilder.badRequestResponse("Failed to get access token from Google", StatusCodeEnum.USER0200);
        }

    }

    public ResponseEntity<ApiResponse<UserLoginResponse>> verifyEmail(EmailVerificationRequest emailVerificationRequest) {
        Optional<VerificationCode > verificationCode = verificationCodeRepository
                .findByEmailAndCode(emailVerificationRequest.getEmail(), emailVerificationRequest.getCode());

        User user = userRepository.findByEmail(emailVerificationRequest.getEmail());

        if (verificationCode.isEmpty()) {
            return ResponseBuilder.badRequestResponse("Invalid verification code", StatusCodeEnum.USER0200);
        }

        if (verificationCode.get().getExpiredAt().isBefore(LocalDateTime.now())) {
            return ResponseBuilder.badRequestResponse("Verification code has expired", StatusCodeEnum.USER0200);
        }

        if (verificationCode.get().getConfirmedAt() != null) {
            return ResponseBuilder.badRequestResponse("Email has been verified", StatusCodeEnum.USER0200);
        }

        if (user == null) {
            return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
        }

        user.setActivated(true);
        verificationCode.get().setConfirmedAt(LocalDateTime.now());

        userRepository.save(user);
        verificationCodeRepository.save(verificationCode.get());

        UserResponse userResponse = UserResponse.from(user);

        UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                .userInfo(userResponse)
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.refreshToken(user))
                .build();

        return ResponseBuilder.successResponse("Email verified",userLoginResponse, StatusCodeEnum.USER0200);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredVerificationCodes() {
        LocalDateTime now = LocalDateTime.now();
        verificationCodeRepository.deleteAllByExpiredAtBefore(now);
    }

    public void getAccessToken(OauthToken code) {

    }

}
