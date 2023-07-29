package com.beloved.identityservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginMessage {
    private String responseCode;
    private String responseMessage;
    private String status;
}
