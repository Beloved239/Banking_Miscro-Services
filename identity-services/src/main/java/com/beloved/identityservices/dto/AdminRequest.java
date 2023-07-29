package com.beloved.identityservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String email;
    private String phoneNumber;
    private String password;
    private LocalDate dateOfBirth;
}
