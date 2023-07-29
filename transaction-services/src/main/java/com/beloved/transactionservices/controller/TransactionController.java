package com.beloved.transactionservices.controller;

import com.beloved.transactionservices.dto.StatementRequest;
import com.beloved.transactionservices.dto.TransactionDTO;
import com.beloved.transactionservices.dto.TransactionHistory;
import com.beloved.transactionservices.entity.Transaction;
import com.beloved.transactionservices.service.TransactionService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("api/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveTransaction(@RequestBody TransactionDTO transactionDTO){
        transactionService.saveTransaction(transactionDTO);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TransactionDTO>> getAllTransaction(){
        return ResponseEntity.ok(transactionService.getAllTransaction());
    }

    @GetMapping("/single/transaction")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TransactionDTO>> getTransactionByAccountNumber(@RequestBody TransactionHistory transactionHistory){
        return ResponseEntity.ok(transactionService.getByAccountNumber(transactionHistory));
    }

    @GetMapping("/statement")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> generateStatement(@RequestBody StatementRequest statementRequest) throws DocumentException, FileNotFoundException {
        return transactionService.generateStatement(statementRequest);
    }
}
