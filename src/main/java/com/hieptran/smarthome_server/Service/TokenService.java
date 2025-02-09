package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.model.OauthToken;
import com.hieptran.smarthome_server.repository.OAuthTokenRepository;
import com.hieptran.smarthome_server.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final OAuthTokenRepository oAuthTokenRepository;

    public String saveToken(String name, String accessToken, String refreshToken) {
        String encryptedAccessToken = EncryptionUtils.encrypt(accessToken);
        String encryptedRefreshToken = EncryptionUtils.encrypt(refreshToken);

        OauthToken oauthToken = OauthToken.builder()
                .name(name)
                .accessToken(encryptedAccessToken)
                .refreshToken(encryptedRefreshToken)
                .build();

        return oAuthTokenRepository.save(oauthToken).getId().toString();
    }

}
