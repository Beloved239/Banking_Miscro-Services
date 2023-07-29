package com.beloved.transactionservices.service.Impl;

import com.beloved.transactionservices.dto.Response;
import com.beloved.transactionservices.dto.StatementRequest;
import com.beloved.transactionservices.dto.TransactionDTO;
import com.beloved.transactionservices.dto.TransactionHistory;
import com.beloved.transactionservices.entity.Transaction;
import com.beloved.transactionservices.repository.TransactionRepository;
import com.beloved.transactionservices.service.TransactionService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    private static final String FILE = "C:\\Users\\oluwatobi.adebanjo\\OneDrive - Africa Prudential\\Documents\\Java Projects\\Bank Statement\\accountStatement.pdf";

    public TransactionServiceImpl (TransactionRepository transactionRepository){
        this.transactionRepository=transactionRepository;
    }
    @Override
    public void saveTransaction(TransactionDTO transactionDTO) {
       Transaction newTransaction = Transaction.builder()
               .transactionType(transactionDTO.getTransactionType())
               .accountNumber(transactionDTO.getAccountNumber())
               .amount(transactionDTO.getAmount())
               .build();
       transactionRepository.save(newTransaction);
    }
    @Override
    public List<TransactionDTO> getAllTransaction() {
        List <Transaction> transaction = transactionRepository.findAll();
        List<TransactionDTO> response = new ArrayList<>();
        for (Transaction transaction1: transaction){
            response.add(TransactionDTO.builder()
                            .transactionType(transaction1.getTransactionType())
                            .accountNumber(transaction1.getAccountNumber())
                            .amount(transaction1.getAmount())
                    .build());
        }
        return response;
    }
    @Override
    public Transaction fetchByAccountNumber(TransactionHistory transactionHistory) {
     List<Response> responses = new ArrayList<>();

        Transaction transaction = (Transaction) transactionRepository.findByAccountNumber(transactionHistory.getAccountNumber());
     TransactionDTO transactionDTO = new TransactionDTO();

      transactionDTO.setAccountNumber(transaction.getAccountNumber());
      transactionDTO.setTransactionType(transaction.getTransactionType());
      transactionDTO.setAmount(transaction.getAmount());

       return transaction;
    }

    @Override
    public List<TransactionDTO> getByAccountNumber(TransactionHistory transactionHistory) {
        List<Transaction> transactionList = transactionRepository.findByAccountNumber(transactionHistory
                .getAccountNumber());
        List<TransactionDTO> transactionDTOList = new ArrayList<>();

        for (Transaction transaction: transactionList){

            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .transactionType(transaction.getTransactionType())
                    .accountNumber(transaction.getAccountNumber())
                    .amount(transaction.getAmount())
                    .build();
            transactionDTOList.add(transactionDTO);
        }
        return transactionDTOList;
    }



    @Override
    public List<Transaction> generateStatement(StatementRequest statementRequest) throws FileNotFoundException, DocumentException {

        LocalDate startDates = LocalDate.parse(statementRequest.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDates = LocalDate.parse(statementRequest.getEndDate(), DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(statementRequest.getAccountNumber()))
                .filter(transaction -> transaction.getCreatedAt().isEqual(startDates)).filter(transaction -> transaction.getCreatedAt().isEqual(endDates)).toList();


//        User user = userRepository.findByAccountNumber(accountNumber);
//
//        String customerName = user.getFirstName()+" "+ user.getLastName()+" "+ user.getOtherName();


        Rectangle size = new Rectangle(PageSize.A4);
        Document document = new Document(size);
        log.info("setting document size");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable statementInfo = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("VDF Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);


        PdfPCell bankAddress = new PdfPCell(new Phrase("30 Ilupeju,Lagos Nigeria"));
        bankAddress.setBorder(0);
        statementInfo.addCell(bankName);
        statementInfo.addCell(bankAddress);


//        PdfPTable secondHeading = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: "+statementRequest.getStartDate()));
        customerInfo.setBorder(0);


        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);

        PdfPCell stopDate = new PdfPCell(new Phrase("End Date"+ statementRequest.getStartDate()));
        stopDate.setBorder(0);

        PdfPCell customerDetails = new PdfPCell(new Phrase("Customer Name: "));
        customerDetails.setBorder(0);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address "));
        address.setBorder(0);


        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBorder(0);
        transactionAmount.setBackgroundColor(BaseColor.BLUE);

        PdfPCell transactionAccountNumber = new PdfPCell(new Phrase("ACCOUNT NUMBER"));
        transactionAccountNumber.setBackgroundColor(BaseColor.BLUE);
        transactionAccountNumber.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(transactionAccountNumber);

        transactionList.forEach(transactions -> {
            transactionTable.addCell(new Phrase(transactions.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transactions.getTransactionType()));
            transactionTable.addCell(new Phrase(transactions.getAmount().toString()));
            transactionTable.addCell(new Phrase(transactions.getAccountNumber()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(statementRequest.getEndDate());
        statementInfo.addCell("customerName");
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(statementInfo);
        document.add(transactionTable);
        document.add(customerInfo);
        document.close();

        return transactionList;

    }
}
