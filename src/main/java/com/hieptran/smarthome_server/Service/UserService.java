package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.config.security.UserDetailsImpl;
import com.hieptran.smarthome_server.dto.ApiResponse;
import com.hieptran.smarthome_server.dto.StatusCodeEnum;
import com.hieptran.smarthome_server.dto.builder.ResponseBuilder;
import com.hieptran.smarthome_server.dto.requests.*;
import com.hieptran.smarthome_server.dto.responses.AccessTokenResponse;
import com.hieptran.smarthome_server.dto.responses.UserLoginResponse;
import com.hieptran.smarthome_server.dto.responses.UserResponse;
import com.hieptran.smarthome_server.model.OauthToken;
import com.hieptran.smarthome_server.model.User;
import com.hieptran.smarthome_server.model.VerificationCode;
import com.hieptran.smarthome_server.repository.OAuthTokenRepository;
import com.hieptran.smarthome_server.repository.UserRepository;
import com.hieptran.smarthome_server.utils.EncryptionUtils;
import freemarker.template.TemplateException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final OAuthTokenRepository oAuthTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final TokenService tokenService;

    private final EmailService emailService;

    public ResponseEntity<ApiResponse<UserLoginResponse>> createUser(UserCreateRequest userRequest) {
        try {
            if (userRepository.existsByUsername(userRequest.getUsername())) {
                return ResponseBuilder.badRequestResponse("Username is already taken", StatusCodeEnum.EXCEPTION);
            }

            if (userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseBuilder.badRequestResponse("Email is already taken", StatusCodeEnum.EXCEPTION);
            }

            if (!Objects.equals(userRequest.getPassword(), userRequest.getConfirmPassword())) {
                return ResponseBuilder.badRequestResponse("Password and confirm password do not match", StatusCodeEnum.USER0200);
            }

            String avatar = userRequest.getAvatar() != null ? userRequest.getAvatar() : "";

            User user = User.builder()
                    .username(userRequest.getUsername())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .email(userRequest.getEmail())
//                    .displayName(userRequest.getDisplayName())
                    .avatar(avatar)
                    .role("client")
                    .isActivated(false)
                    .build();

            userRepository.save(user);

            String code = EncryptionUtils.generateVerificationCode();

            VerificationCode verificationCode = VerificationCode.builder()
                    .code(code)
                    .email(user.getEmail())
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusMinutes(10))
                    .build();

            emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), code, 10);

            UserResponse userResponse = UserResponse.from(user);

            UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                    .userInfo(userResponse)
                    .accessToken(jwtService.generateToken(user))
                    .refreshToken(jwtService.refreshToken(user))
                    .build();

            return ResponseBuilder.successResponse("User created", userLoginResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<UserLoginResponse>> oAuthGoogleUserCreate(
            OAuthGoogleUserCreateRequest oAuthGoogleUserCreateRequest,
            OAuthTokenRequest oAuthTokenRequest
            ) {
        try {
//            String tokenId = tokenService.saveToken(
//                    oAuthGoogleUserCreateRequest.getEmail(),
//                    oAuthTokenRequest.getAccessToken(),
//                    oAuthTokenRequest.getRefreshToken()
//            );

            String tokenId = "";

            if (userRepository.existsByEmail(oAuthGoogleUserCreateRequest.getEmail())) {
                User user = userRepository.findByEmail(oAuthGoogleUserCreateRequest.getEmail());

                if (user.getOauthToken() == null) {
                    user.setOauthToken(new ArrayList<>());
                }

                user.setGoogleId(oAuthGoogleUserCreateRequest.getGoogleId());

                return getApiResponseResponseEntity(user, tokenId);
            }

            User user = User.builder()
                    .email(oAuthGoogleUserCreateRequest.getEmail())
                    .displayName(oAuthGoogleUserCreateRequest.getDisplayName())
                    .avatar(oAuthGoogleUserCreateRequest.getAvatar())
                    .googleId(oAuthGoogleUserCreateRequest.getGoogleId())
                    .role("client")
                    .isActivated(true)
                    .build();

            return getApiResponseResponseEntity(user, tokenId);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    @NotNull
    private ResponseEntity<ApiResponse<UserLoginResponse>> getApiResponseResponseEntity(User user, String tokenId) {
//        user.getOauthToken().add(tokenId);

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);

        String refreshToken = jwtService.refreshToken(user);

        UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                .userInfo(UserResponse.from(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseBuilder.successResponse("Login success", userLoginResponse, StatusCodeEnum.USER1200);
    }

    public ResponseEntity<ApiResponse<UserLoginResponse>> oAuthGoogleUserLogin (OAuthGoogleUserLoginRequest oAuthGoogleUserLoginRequest) {
        try {
            User user = oAuthGoogleUserLoginRequest.getUser();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            String accessToken = jwtService.generateToken(user);

            String refreshToken = jwtService.refreshToken(user);

            UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                    .userInfo(UserResponse.from(user))
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseBuilder.successResponse("Login success", userLoginResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<UserLoginResponse>> login(AuthenticationRequest authenticationRequest) {
        try {
            Optional<User> user = userRepository.findByUsername(authenticationRequest.getUsername());

            if (user.isEmpty()) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.get().getPassword())) {
                return ResponseBuilder.badRequestResponse("Invalid password", StatusCodeEnum.USER0200);
            }

            if (!user.get().isActivated()) {
                return ResponseBuilder.badRequestResponse("User is not activated", StatusCodeEnum.USER0200);
            }

            String accessToken = jwtService.generateToken(user.get());

            String refreshToken = jwtService.refreshToken(user.get());

            UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                    .userInfo(UserResponse.from(user.get()))
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseBuilder.successResponse("Login success", userLoginResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<UserResponse>> updateUser(UserInfoUpdateRequest userInfoUpdateRequest) {
        try {
            User user = getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            if (userInfoUpdateRequest.getUsername() != null && !userInfoUpdateRequest.getUsername().equals(user.getUsername())) {
                if (userRepository.existsByUsername(userInfoUpdateRequest.getUsername())) {
                    return ResponseBuilder.badRequestResponse("Username is already taken", StatusCodeEnum.EXCEPTION);
                }

                user.setUsername(userInfoUpdateRequest.getUsername());
            }

            if (userInfoUpdateRequest.getEmail() != null && !userInfoUpdateRequest.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(userInfoUpdateRequest.getEmail())) {
                    return ResponseBuilder.badRequestResponse("Email is already taken", StatusCodeEnum.EXCEPTION);
                }

                user.setEmail(userInfoUpdateRequest.getEmail());
            }

            if (userInfoUpdateRequest.getDisplayName() != null && !userInfoUpdateRequest.getDisplayName().equals(user.getDisplayName())) {
                user.setDisplayName(userInfoUpdateRequest.getDisplayName());
            }

            if (userInfoUpdateRequest.getPhone() != null && !userInfoUpdateRequest.getPhone().equals(user.getPhone())) {
                if (userRepository.existsByPhone(userInfoUpdateRequest.getPhone())) {
                    return ResponseBuilder.badRequestResponse("Phone is already taken", StatusCodeEnum.EXCEPTION);
                }
                user.setPhone(userInfoUpdateRequest.getPhone());
            }

            if (userInfoUpdateRequest.getAbout() != null && !userInfoUpdateRequest.getAbout().equals(user.getAbout())) {
                user.setAbout(userInfoUpdateRequest.getAbout());
            }

            if (userInfoUpdateRequest.getDefaultHomeId() != null && !userInfoUpdateRequest.getDefaultHomeId().equals(user.getDefaultHomeId())) {
                user.setDefaultHomeId(userInfoUpdateRequest.getDefaultHomeId());
            }

            userRepository.save(user);

            UserResponse userResponse = UserResponse.from(user);

            return ResponseBuilder.successResponse("Update user success", userResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<UserResponse>> updateUserPassword(UserPasswordUpdateRequest userPasswordUpdateRequest) {
        try {
            User user = getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            if (!passwordEncoder.matches(userPasswordUpdateRequest.getOldPassword(), user.getPassword())) {
                return ResponseBuilder.badRequestResponse("Old password is incorrect", StatusCodeEnum.USER0200);
            }

            if (!Objects.equals(userPasswordUpdateRequest.getNewPassword(), userPasswordUpdateRequest.getConfirmPassword())) {
                return ResponseBuilder.badRequestResponse("New password and confirm password do not match", StatusCodeEnum.USER0200);
            }

            user.setPassword(passwordEncoder.encode(userPasswordUpdateRequest.getNewPassword()));

            userRepository.save(user);

            UserResponse userResponse = UserResponse.from(user);

            return ResponseBuilder.successResponse("Update user password success", userResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<Objects>> deleteUser() {
        try {
            User user = getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            userRepository.deleteById(user.getId().toString());

            return ResponseBuilder.successResponse("Delete user success", StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo() {
        try {
            User user = getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            UserResponse userResponse = UserResponse.from(user);

            return ResponseBuilder.successResponse("Get user info success", userResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<UserResponse>> setHomeDefault(HomeDefaultRequest homeDefaultRequest) {
        try {
            User user = getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            if (homeDefaultRequest.getHomeId() != null) {
                user.setDefaultHomeId(homeDefaultRequest.getHomeId());
            }

            userRepository.save(user);

            UserResponse userResponse = UserResponse.from(user);

            return ResponseBuilder.successResponse("Set default home success", userResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<Boolean>> introspectToken(HttpServletRequest request) {
        try {
            User user = getUserFromContext();

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            String token = jwtService.getJwtFromRequest(request);

            return ResponseBuilder.successResponse("Token is valid", jwtService.validateToken(token), StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public ResponseEntity<ApiResponse<AccessTokenResponse>> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (refreshToken == null) {
                return ResponseBuilder.badRequestResponse("Refresh token is required", StatusCodeEnum.USER0200);
            }

            if (!jwtService.validateToken(refreshToken)) {
                return ResponseBuilder.badRequestResponse("Refresh token is invalid", StatusCodeEnum.USER0200);
            }

            String userId = jwtService.getUserIdFromToken(refreshToken);

            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseBuilder.badRequestResponse("User not found", StatusCodeEnum.USER0200);
            }

            String accessToken = jwtService.generateToken(user);

            AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
                    .accessToken(accessToken)
                    .build();

            return ResponseBuilder.successResponse("Refresh token success", accessTokenResponse, StatusCodeEnum.USER1200);
        } catch (Exception e) {
            return ResponseBuilder.badRequestResponse(e.getMessage(), StatusCodeEnum.USER0200);
        }
    }

    public User getUserFromContext(){
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            log.info("User details by authentication: " + userDetails);

            return userDetails.getUser();
        } catch (Exception e) {
            log.error("Get user from context failed: ", e.getMessage());

            return null;
        }
    }

    public ResponseEntity<Boolean> isEmailExist(String email) {
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }

    public ResponseEntity<Boolean> isUsernameExist(String username) {
        return ResponseEntity.ok(userRepository.existsByUsername(username));
    }

    public void resendVerificationEmail(String email) throws TemplateException, IOException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return;
        }

        emailService.sendVerificationEmail(
                email,
                user.getUsername(),
                EncryptionUtils.generateVerificationCode(),
                10);
    }
}
