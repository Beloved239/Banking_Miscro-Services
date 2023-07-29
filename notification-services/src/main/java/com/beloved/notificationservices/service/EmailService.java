package com.beloved.notificationservices.service;


import com.beloved.notificationservices.dto.EmailDetails;

public interface EmailService {
    String sendSimpleEmail(EmailDetails emailDetails);

    String sendEmailWithAttachment(EmailDetails emailDetails);
}
