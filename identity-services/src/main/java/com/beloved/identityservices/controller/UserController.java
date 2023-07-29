package com.beloved.identityservices.controller;

import com.beloved.identityservices.dto.*;
import com.beloved.identityservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/registerUser")
    public Response registerUser(@RequestBody UserRequest userRequest)  {
        return userService.registerUser(userRequest);
    }

    @PostMapping("/register/admin")
    public Response registerAdmin(@RequestBody AdminRequest adminRequest){
        return userService.registerAdmin(adminRequest);
    }

    @PostMapping("/user/new/login")
    public ResponseEntity<LoginMessage> signIn(@RequestBody LoginDTO loginDTO){
        return userService.loginEmployee(loginDTO);
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Response> fetchAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping(path = "/getUserById/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Response getUserById(@PathVariable(name = "id") Long userId){
        return userService.fetchUser(userId);
    }

    @GetMapping( "/balanceEnquiry/accountNumber")
    @PreAuthorize("hasAuthority('USER')")
    public Response balanceEnquiry(@RequestParam(name = "accountNumber") String accountNumber){
        return userService.balanceEnquiry(accountNumber);
    }

    @GetMapping("/nameEnquiry")
    @PreAuthorize("hasAuthority('USER')")
    public Response nameEnquiry(@RequestParam(name = "accountNumber")String accountNumber){
        return userService.nameEnquiry(accountNumber);
    }

    @GetMapping("/user/welcome")
    @PreAuthorize("hasAuthority('USER')")
    public String welcomePage(){
        return "You're Welcome to Identity Management Services";
    }

    @PostMapping("/reset/password")
    public ResponseEntity<LoginMessage> resetPassword(@RequestBody LoginDTO loginDTO){
        return userService.resetPassword(loginDTO);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER')")
    public Response creditRequest(@RequestBody TransactionRequest transactionRequest){
        return userService.credit(transactionRequest);
    }

    @PutMapping("/debit")
    @PreAuthorize("hasAuthority('USER')")
    public Response debitRequest(@RequestBody TransactionRequest transactionRequest){
        return userService.debit(transactionRequest);
    }

    @PutMapping("/debit/transfer")
    @PreAuthorize("hasAuthority('USER')")
    public Response transferRequest(@RequestBody TransferRequest transferRequest){
        return userService.transfer(transferRequest);
    }



}
