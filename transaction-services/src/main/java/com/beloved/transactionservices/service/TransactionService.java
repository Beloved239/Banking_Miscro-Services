package com.beloved.transactionservices.service;


import com.beloved.transactionservices.dto.StatementRequest;
import com.beloved.transactionservices.dto.TransactionDTO;
import com.beloved.transactionservices.dto.TransactionHistory;
import com.beloved.transactionservices.entity.Transaction;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.util.List;


public interface TransactionService {
    void saveTransaction(TransactionDTO transactionDTO);
    List<TransactionDTO> getAllTransaction();

    Transaction fetchByAccountNumber(TransactionHistory transactionHistory);
    List <TransactionDTO> getByAccountNumber(TransactionHistory transactionHistory);

    List<Transaction> generateStatement(StatementRequest statementRequest) throws FileNotFoundException, DocumentException;
}
