package com.hieptran.smarthome_server.dto.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationRequest {
    private String email;

    private String code;
}
