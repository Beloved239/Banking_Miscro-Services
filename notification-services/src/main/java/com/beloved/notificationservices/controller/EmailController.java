package com.beloved.notificationservices.controller;

import com.beloved.notificationservices.dto.EmailDetails;
import com.beloved.notificationservices.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/notification")
public class EmailController {
    @Autowired
    EmailService emailService;

    @PostMapping("/senderMail")
    public String sendEmail(@RequestBody EmailDetails emailDetails){
        return emailService.sendSimpleEmail(emailDetails);
    }
}
