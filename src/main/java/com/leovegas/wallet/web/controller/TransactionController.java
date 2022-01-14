package com.leovegas.wallet.web.controller;

import com.leovegas.wallet.model.constant.TransactionType;
import com.leovegas.wallet.model.dao.TransactionDAO;
import com.leovegas.wallet.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {

    private final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    TransactionService transactionService;

    @GetMapping(path = "/{playerId}/history")
    public ResponseEntity<List<TransactionDAO>> getTransactionHistoryByPlayerId(@PathVariable Long playerId) {
        log.info(String.format("/transaction/%d/history GET endpoint called.", playerId));
        return new ResponseEntity<>(transactionService.findAllByPlayerId(playerId), HttpStatus.OK);
    }

    @Transactional
    @PostMapping(path = "/{playerId}/credit")
    public ResponseEntity<TransactionDAO> creditByPlayerId(@PathVariable(name = "playerId") Long playerId,
                                                           @RequestParam(name = "amount", required = true) Double amount,
                                                           @RequestParam(name = "transactionId", required = true) String transactionId) {
        log.info(String.format("/transaction/%d/credit?amount=%f&transactionId=%s POST endpoint called.", playerId, amount, transactionId));
        return new ResponseEntity<>(transactionService.makeTransaction(playerId, amount, TransactionType.CREDIT, transactionId), HttpStatus.CREATED);
    }

    @PostMapping(path = "/{playerId}/debit")
    @Transactional
    public ResponseEntity<TransactionDAO> debitByPlayerId(@PathVariable(name = "playerId") Long playerId,
                                                          @RequestParam(name = "amount", required = true) Double amount,
                                                          @RequestParam(name = "transactionId", required = true) String transactionId) {
        log.info(String.format("/transaction/%d/debit?amount=%f&transactionId=%s POST endpoint called.", playerId, amount, transactionId));
        return new ResponseEntity<>(transactionService.makeTransaction(playerId, amount, TransactionType.DEBIT, transactionId), HttpStatus.CREATED);
    }

}
