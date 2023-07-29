package com.beloved.identityservices.service.serviceImpl;

import com.beloved.identityservices.Entity.User;
import com.beloved.identityservices.dto.*;
import com.beloved.identityservices.repository.UserRepository;
import com.beloved.identityservices.service.UserService;
import com.beloved.identityservices.utils.ResponseUtils;
import com.beloved.identityservices.utils.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final WebClient webClient;




    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, WebClient webClient){
        this.userRepository =userRepository;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.webClient = webClient;
    }
    @Override
    @Transactional
    public Response registerUser(UserRequest userRequest) {

        boolean isEmailExist = userRepository.existsByEmail(userRequest.getEmail());
        if (isEmailExist) {
            return Response.builder()
                    .responseCode(ResponseUtils.USER_EXISTS_CODE)
                    .responseMessage(ResponseUtils.USER_EXISTS_MESSAGE)
                    .data(null)
                    .build();
        }
            User user = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .otherName(userRequest.getOtherName())
                    .gender(userRequest.getGender())
                    .address(userRequest.getAddress())
                    .stateOfOrigin(userRequest.getStateOfOrigin())
                    .accountNumber(ResponseUtils.generateAccountNumber(ResponseUtils.LENGTH_OF_ACCOUNT_NUMBER))
                    .accountBalance(userRequest.getAccountBalance())
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                    .role(Role.USER)
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .status("ACTIVE")
                    .dateOfBirth(userRequest.getDateOfBirth())
                    .build();
            User savedUser = userRepository.save(user);

        String accountDetails = savedUser.getFirstName()+ " "+ savedUser.getLastName()+ " " + savedUser.getOtherName()+
                "\nAccount Number:" + savedUser.getAccountNumber();

        EmailDetails messages = EmailDetails.builder()
                .subject("Account Created Successfully")
                .recipient(savedUser.getEmail())
                .messageBody("Congratulations!, Your account has been successfully created! Kindly find your details below: \n" +
                        accountDetails)
                .build();

         webClient.post()
                .uri("http://localhost:9091/api/notification/senderMail")
                 .body(BodyInserters.fromValue(messages))
                .retrieve()
                .bodyToMono(String.class)
                .block();




//        SMSRequest message =
//        SMSRequest SMSMessage = SMSRequest.builder()
//                .destinationSmsNumber(userRequest.getPhoneNumber())
//                .smsMessage("Congratulations!, Your account has been successfully created! Kindly find your details below: \n" +
//                        accountDetails)
//                .build();
//        smsService.sendSMS(SMSMessage);

        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.USER_REGISTERED_SUCCESS)
                .data(Data.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() +" "+ savedUser.getLastName()+" "+savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public Response registerAdmin(AdminRequest adminRequest) {
        boolean isEmailExist = userRepository.existsByEmail(adminRequest.getEmail());
        if (isEmailExist) {
            return Response.builder()
                    .responseCode(ResponseUtils.USER_EXISTS_CODE)
                    .responseMessage(ResponseUtils.USER_EXISTS_MESSAGE)
                    .data(null)
                    .build();
        }

        User admin = User.builder()
                .firstName(adminRequest.getFirstName())
                .lastName(adminRequest.getLastName())
                .gender(adminRequest.getGender())
                .role(Role.ADMIN)
                .accountNumber(ResponseUtils.generateAccountNumber(ResponseUtils.LENGTH_OF_ACCOUNT_NUMBER))
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .address(adminRequest.getAddress())
                .dateOfBirth(adminRequest.getDateOfBirth())
                .email(adminRequest.getEmail())
                .password(passwordEncoder.encode(adminRequest.getPassword()))
                .phoneNumber(adminRequest.getPhoneNumber())
                .build();

        userRepository.save(admin);
        EmailDetails message = EmailDetails.builder()
                .recipient(adminRequest.getEmail())
                .subject("Account Created Successfully")
                .messageBody(admin.getFirstName()+" "+admin.getLastName()+" "+ " Your account has been created Successfully")
                .build();

        webClient.post()
                .uri("http://localhost:9091/api/notification/senderMail")
                .body(BodyInserters.fromValue(message))
                .retrieve()
                .bodyToMono(String.class)
                .block();
//
//        SMSRequest smsRequest = SMSRequest.builder()
//                .destinationSmsNumber(admin.getPhoneNumber())
//                .smsMessage(admin.getFirstName()+" "+admin.getLastName()+" "+ "Account Created Successfully")
//                .build();
//        smsService.sendSMS(smsRequest);

        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .data(Data.builder()
                        .accountName(adminRequest.getFirstName()+" "+ admin.getLastName())
                        .accountNumber(admin.getAccountNumber())
                        .build())

                .build();
    }


    @Override
    public ResponseEntity<LoginMessage> loginEmployee(LoginDTO loginDTO) {
        boolean exists = userRepository.existsByEmail(loginDTO.getEmail());
        if (!exists ){
            return ResponseEntity.ok(LoginMessage.builder()
                    .responseCode(ResponseUtils.UNSUCCESSFUL_LOGIN_RESPONSE_CODE)
                    .responseMessage("USERNAME_OR_PASSWORD_INCORRECT_MESSAGE")
                    .build());
        }else {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ResponseEntity<>(
                    LoginMessage.builder()
                            .responseCode(ResponseUtils.SUCCESSFUL_LOGIN_RESPONSE_CODE)
                            .responseMessage(ResponseUtils.SUCCESSFUL_LOGIN_MESSAGE)
                            .status("True")
                            .build(),HttpStatus.CREATED);
        }
    }

    @Override
    public ResponseEntity<LoginMessage> resetPassword(LoginDTO loginDTO) {
        if (!userRepository.existsByEmail(loginDTO.getEmail())){
            return ResponseEntity.ok(LoginMessage.builder()
                    .responseCode(ResponseUtils.UNSUCCESSFUL_LOGIN_RESPONSE_CODE)
                    .responseMessage(ResponseUtils.EMAIL_DOES_NOT_EXIST_MESSAGE)
                            .status("True")
                    .build());
        }else {
            User user = userRepository.findByEmail(loginDTO.getEmail()).get();
            String encoder = passwordEncoder.encode(loginDTO.getPassword());
            user.setPassword(encoder);
            userRepository.save(user);

            return ResponseEntity.ok(LoginMessage.builder()
                    .responseCode(ResponseUtils.SUCCESSFUL_LOGIN_RESPONSE_CODE)
                    .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                    .status("True")
                    .build());
        }
    }

    @Override
    public List<Response> getAllUsers() {
        List<User> userList = userRepository.findAll();
        List<Response> responses = new ArrayList<>();

        for (User user: userList){
            responses.add(Response.builder()
                            .responseCode(ResponseUtils.SUCCESS)
                            .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                            .data(Data.builder()
                                    .accountName(user.getFirstName()+" "+ user.getLastName()+" "+ user.getOtherName())
                                    .accountNumber(user.getAccountNumber())
                                    .accountBalance(user.getAccountBalance())
                                    .build())
                    .build());
        }
        return responses;
    }
    @Override
    public Response fetchUser(Long userId) {
        if (!userRepository.existsById(userId)){
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND_MESSAGE)
                    .data(null)
                    .build();
        }
        User user = userRepository.findById(userId).get();
        return Response.builder()
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .responseCode(ResponseUtils.SUCCESS)
                .data(Data.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName()+" "+user.getLastName()+" "+ user.getOtherName())
                        .accountBalance(user.getAccountBalance())
                        .build())
                .build();
    }
    @Override
    public Response balanceEnquiry(String accountNumber) {
        boolean isAccountNumberExist = userRepository.existsByAccountNumber(accountNumber);
        if (!isAccountNumberExist){
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND_MESSAGE)
                    .data(null)
                    .build();
        }

        User user = userRepository.findByAccountNumber(accountNumber);
        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .data(Data.builder()
                        .accountName(user.getFirstName()+" "+user.getLastName()+" "+ user.getOtherName())
                        .accountNumber(user.getAccountNumber())
                        .accountBalance(user.getAccountBalance())
                        .build())
                .build();
    }
    @Override
    public Response nameEnquiry(String accountNumber) {
        boolean isAccountNumberExist = userRepository.existsByAccountNumber(accountNumber);
        if (!isAccountNumberExist) {
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND_MESSAGE)
                    .data(null)
                    .build();
        }
        User user = userRepository.findByAccountNumber(accountNumber);
        return Response.builder()
                .responseCode(ResponseUtils.SUCCESS)
                .responseMessage(ResponseUtils.SUCCESS_MESSAGE)
                .data(Data.builder()
                        .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                        .accountNumber(null)
                        .accountBalance(null)
                        .build())
                .build();
    }

    @Override
    public Response credit(TransactionRequest transactionRequest) {
        User receivingUser= userRepository.findByAccountNumber(transactionRequest.getAccountNumber());
        if (!userRepository.existsByAccountNumber(transactionRequest.getAccountNumber())){
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND_MESSAGE)
                    .data(null)
                    .build();
        }
        receivingUser.setAccountBalance(receivingUser.getAccountBalance().add(transactionRequest.getAmount()));
        User savedUser = userRepository.save(receivingUser);
//        TransactionDTO transactionDTO =new TransactionDTO();
//        transactionDTO.setTransactionType("credit");
//        transactionDTO.setAmount(transactionRequest.getAmount());
//        transactionDTO.setAccountNumber(transactionRequest.getAccountNumber());
//        transactionService.saveTransaction(transactionDTO);

        String accountDetails = savedUser.getFirstName()+ " "+ savedUser.getLastName()+ " " + savedUser.getOtherName()+
                "\nAccount Number: " + savedUser.getAccountNumber();
        //Email alert
        EmailDetails message = EmailDetails.builder()
                .recipient(receivingUser.getEmail())
                .subject("DVB(Dollar Virtual Bank) CREDIT Transaction Notification")
                .messageBody("This is to notify you that a transaction has occurred on your account with DVB with " +
                        "details below: \n" +
                        "Account Name: " + accountDetails+"\n"
//                        +"Transaction Type: "+ transactionDTO.getTransactionType()+"\n"
                        +"Transaction Amount: "+ transactionRequest.getAmount()+"\n"
                        +"Transaction currency: "+ "NGN"+"\n"
                        +"Transaction Balance: "+ receivingUser.getAccountBalance())
                .build();
//        emailService.sendSimpleEmail(message);

        webClient.post()
                .uri("http://localhost:9091/api/notification/senderMail")
                .body(BodyInserters.fromValue(message))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return Response.builder()
                .responseCode(ResponseUtils.SUCCESSFUL_TRANSACTION)
                .responseMessage(ResponseUtils.ACCOUNT_CREDITED)
                .data(Data.builder()
                        .accountNumber(receivingUser.getAccountNumber())
                        .accountBalance(receivingUser.getAccountBalance())
                        .accountName(receivingUser.getFirstName()+" "+ receivingUser.getLastName()+ " "+ receivingUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public Response debit(TransactionRequest transactionRequest) {
        User debitinguser = userRepository.findByAccountNumber(transactionRequest.getAccountNumber());
        if (!userRepository.existsByAccountNumber(transactionRequest.getAccountNumber())){
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND_MESSAGE)
                    .data(null)
                    .build();
        }
        if (debitinguser.getAccountBalance().compareTo(transactionRequest.getAmount())> 0) {
            debitinguser.setAccountBalance(debitinguser.getAccountBalance().subtract(transactionRequest.getAmount()));
            userRepository.save(debitinguser);
//            TransactionDTO transactionDTO = new TransactionDTO();
//            transactionDTO.setTransactionType("Debit");
//            transactionDTO.setAmount(transactionRequest.getAmount());
//            transactionDTO.setAccountNumber(transactionRequest.getAccountNumber());
//            transactionService.saveTransaction(transactionDTO);
            String accountDetails = debitinguser.getLastName()+" "+ debitinguser.getFirstName()+" "+debitinguser.getOtherName()+
                    "Account Number: "+debitinguser.getAccountNumber();

            EmailDetails message = EmailDetails.builder()
                    .recipient(debitinguser.getEmail())
                    .subject("DVB(Dollar Virtual Bank) DEBIT Transaction Notification")
                    .messageBody("This is to notify you that a transaction has occurred on your account with DVB with details below: \n"
                            +"Account Name: "+ accountDetails+"\n"
//                            + "Transaction Type: "+ transactionDTO.getTransactionType()+"\n"
                            + "Transaction Amount: "+ transactionRequest.getAmount()+"\n"
                            + "Transaction Currency: "+ "NGN"+"\n"
                            + "Available Balance: "+ debitinguser.getAccountBalance())
                    .build();
//            emailService.sendSimpleEmail(message);

            webClient.post()
                    .uri("http://localhost:9091/api/notification/senderMail")
                    .body(BodyInserters.fromValue(message))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return Response.builder()
                    .responseCode(ResponseUtils.SUCCESSFUL_TRANSACTION)
                    .responseMessage(ResponseUtils.ACCOUNT_DEBITED)
                    .data(Data.builder()
                            .accountNumber(debitinguser.getAccountNumber())
                            .accountBalance(debitinguser.getAccountBalance())
                            .accountName(debitinguser.getFirstName() + " " + debitinguser.getLastName() + " " + debitinguser.getOtherName())
                            .build())
                    .build();

        }
        else return Response.builder()
                .responseCode(ResponseUtils.USER_BALANCE_INSUFFICIENT)
                .responseMessage(ResponseUtils.USER_BALANCE_INSUFFICIENT_MESSAGE)
                .data(null)
                .build();
    }

    @Override
    public Response transfer(TransferRequest transferRequest) {
        User debitAccount = userRepository.findByAccountNumber(transferRequest.getSenderAccountNumber());
        User creditAccount = userRepository.findByAccountNumber(transferRequest.getReceiverAccountNumber());
        if (!userRepository.existsByAccountNumber(transferRequest.getSenderAccountNumber())){
            return Response.builder()
                    .responseCode(ResponseUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(ResponseUtils.USER_NOT_FOUND_MESSAGE)
                    .data(null)
                    .build();
        }
        debitAccount.setAccountBalance(debitAccount.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepository.save(debitAccount);
//        TransactionDTO transactionDTO = new TransactionDTO();
//        transactionDTO.setAccountNumber(transactionDTO.getAccountNumber());
//        transactionDTO.setTransactionType("Debit");
//        transactionDTO.setAccountNumber(transferRequest.getSenderAccountNumber());
//        transactionService.saveTransaction(transactionDTO);
        String accountDetails= debitAccount.getLastName()+" "+ debitAccount.getFirstName()+" "+ debitAccount.getOtherName()
                +" "+ debitAccount.getAccountNumber();
//        EmailDetails message = EmailDetails.builder()
//                .recipient(debitAccount.getEmail())
//                .subject("DVB(Dollar Virtual Bank) DEBIT Transaction Notification")
//                .messageBody("This is to notify you that a transaction has occurred on your account with DVB with details below: \n"
//                        +"Account Name "+ accountDetails+"\n"
//                        + "Transaction Type "+ transactionDTO.getTransactionType()+"\n"
//                        + "Transaction Amount "+ transferRequest.getAmount()+"\n"
//                        + "Transaction Currency "+ "NGN"+"\n"
//                        + "Available Balance "+ debitAccount.getAccountBalance())
//                .build();
//        emailService.sendSimpleEmail(message);

        creditAccount.setAccountBalance(creditAccount.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(creditAccount);
//        TransactionDTO transactionDTO1 = new TransactionDTO();
//        transactionDTO1.setAccountNumber(transferRequest.getReceiverAccountNumber());
//        transactionDTO1.setTransactionType("Credit");
//        transactionDTO1.setAmount(transferRequest.getAmount());
//        transactionService.saveTransaction(transactionDTO1);
        String accountDetail= creditAccount.getLastName()+" "+ creditAccount.getFirstName()+" "+ creditAccount.getOtherName()
                +" "+"\nAccount Number:"+ creditAccount.getAccountNumber();
//        EmailDetails emailDetails = EmailDetails.builder()
//                .recipient(creditAccount.getEmail())
//                .subject("DVB(Dollar Virtual Bank) CREDIT Transaction Notification")
//                .messageBody("This is to notify you that a transaction has occurred on your account with DVB with details below: \n"
//                        +"Account Name: "+ accountDetail+"\n"
//                        + "Transaction Type: "+ transactionDTO1.getTransactionType()+"\n"
//                        + "Transaction Amount: "+ transferRequest.getAmount()+"\n"
//                        + "Transaction Currency: "+ "NGN"+"\n"
//                        + "Available Balance: "+ creditAccount.getAccountBalance())
//                .build();
//        emailService.sendSimpleEmail(emailDetails);
        return Response.builder()
                .responseCode(ResponseUtils.SUCCESSFUL_TRANSACTION)
                .responseMessage(ResponseUtils.SUCCESSFUL_TRANSFER_MESSAGE)
                .data(Data.builder()
                        .accountNumber(transferRequest.getReceiverAccountNumber())
                        .accountBalance(creditAccount.getAccountBalance())
                        .accountName(creditAccount.getFirstName()+" "+ creditAccount.getLastName()+" "+ creditAccount.getLastName())
                        .build())
                .build();
    }

}
