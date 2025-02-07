package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.UserCreateRequest;
import com.hieptran.smarthome_server.dto.responses.UserLoginResponse;
import com.hieptran.smarthome_server.dto.responses.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    private final UserService userService;

    public ResponseEntity<ApiResponse<String>> generateAuthUrl() {
        String state = UUID.randomUUID().toString();
//        session.setAttribute("oauth2_state", state);

        String authorizationUrl = UriComponentsBuilder
                .fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
//                .queryParam("state", state)
                // Thêm các tham số tùy chọn
                .queryParam("access_type", "offline") // Để nhận refresh token (nếu cần)
                .queryParam("prompt", "consent") // Yêu cầu người dùng cấp quyền mỗi lần đăng nhập
                .build()
                .toUriString();

        return ResponseBuilder.successResponse("Authorization URL generated", authorizationUrl, StatusCodeEnum.USER0200);
    }

    public ResponseEntity<ApiResponse<UserLoginResponse>> handleGoogleCallback(String code, HttpSession session, HttpServletResponse httpResponse) {
        String storedState = (String) session.getAttribute("oauth2_state");

//        if (state == null || !state.equals(storedState)) {
//            throw new IllegalStateException("Invalid state parameter");
////            return ResponseBuilder.badRequestResponse("Invalid state", StatusCodeEnum.USER1200);
//        }
//
//        // Xóa state sau khi sử dụng
//        session.removeAttribute("oauth2_state");

        String requestBody = UriComponentsBuilder.newInstance()
                .queryParam("code", code) // Thêm authorization code
                .queryParam("client_id", googleClientId) // Thêm client_id
                .queryParam("client_secret", googleClientSecret) // Thêm client_secret
                .queryParam("redirect_uri", googleRedirectUri) // Thêm redirect_uri
                .queryParam("grant_type", "authorization_code") // Thêm grant_type
                .build()
                .toString()
                .replace("?", "");

        // Tạo HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            // Gửi request và nhận response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse response để lấy access token
                String responseBody = response.body();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String accessToken = jsonNode.get("access_token").asText();
                String refreshToken = jsonNode.get("refresh_token").asText();

//              getUserInfoFromGoogle(accessToken);
                HttpRequest userInfoRequest = HttpRequest.newBuilder()
                        .uri(URI.create(googleUserInfoUri))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET() // Sử dụng phương thức GET
                        .build();
                HttpResponse<String> userInfoResponse = httpClient.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());
                String Body = userInfoResponse.body();
                JsonNode userInfoNode = objectMapper.readTree(Body);

                String password = UUID.randomUUID().toString();

                UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                        .email(userInfoNode.get("email").asText())
                        .avatar(userInfoNode.get("picture").asText())
                        .password(password)
                        .confirmPassword(password)
                        .build();

                httpResponse.sendRedirect("http://localhost:5173/dashboard");
                return userService.createUser(userCreateRequest);
            } else {
                String errorResponse = response.body();
                return ResponseBuilder.badRequestResponse("Failed to get access token", StatusCodeEnum.USER1200);
            }

        } catch (Exception e) {
//            e.printStackTrace();
            return ResponseBuilder.badRequestResponse("Failed to get access token", StatusCodeEnum.USER1200);
        }

    }

}
