package com.beloved.identityservices.service;



import com.beloved.identityservices.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    Response registerUser(UserRequest userRequest);
    Response registerAdmin(AdminRequest adminRequest);
    ResponseEntity<LoginMessage> loginEmployee(LoginDTO loginDTO);
    ResponseEntity<LoginMessage> resetPassword(LoginDTO loginDTO);
    List<Response> getAllUsers();
    Response fetchUser(Long userId);
    Response balanceEnquiry(String accountNumber);
    Response nameEnquiry(String accountNumber);

    Response credit(TransactionRequest transactionRequest);
    Response debit(TransactionRequest transactionRequest);
    Response transfer(TransferRequest transferRequest);

}
