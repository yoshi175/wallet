package com.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leovegas.wallet.model.constant.TransactionType;
import com.leovegas.wallet.model.dao.BalanceDAO;
import com.leovegas.wallet.model.dao.CurrencyDAO;
import com.leovegas.wallet.model.dao.PlayerDAO;
import com.leovegas.wallet.model.dao.TransactionDAO;
import com.leovegas.wallet.repository.TransactionRepository;
import com.leovegas.wallet.service.TransactionService;
import com.leovegas.wallet.web.controller.TransactionController;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:walletdb",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TransactionRepository transactionRepository;

    @MockBean
    TransactionService transactionService;


    @Test
    void transactionHistoryExpectResponseStatusOk() throws Exception {
        Long playerId = 1L;
        CurrencyDAO currencyDAO = new CurrencyDAO(1L, "Euro", "EUR", "€");
        BalanceDAO balanceDAO = new BalanceDAO(1L, 200.0, currencyDAO);
        PlayerDAO playerDAO = new PlayerDAO(1L, "Jane", "Doe", "jane.doe@mail.com", balanceDAO);
        TransactionDAO transactionDAO = new TransactionDAO(1L, "abc", 200.0, "CREDIT", 200.0, playerDAO, currencyDAO);


        when(transactionService.findAllByPlayerId(playerId)).thenReturn(List.of(transactionDAO));
        mockMvc.perform(get("/transaction/1/history"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].transactionId", is("abc")));
    }

    @Test
    void creditTransactionExpectResponseStatusCreated() throws Exception {
        Long playerId = 1L;
        Double amount = 200.0;
        TransactionType transactionType = TransactionType.CREDIT;
        String transactionId = "abc";

        CurrencyDAO currencyDAO = new CurrencyDAO(1L, "Euro", "EUR", "€");
        BalanceDAO balanceDAO = new BalanceDAO(1L, amount, currencyDAO);
        PlayerDAO playerDAO = new PlayerDAO(1L, "Jane", "Doe", "jane.doe@mail.com", balanceDAO);
        TransactionDAO transactionDAO = new TransactionDAO(1L, transactionId, 200.0, "CREDIT", amount, playerDAO, currencyDAO);

        when(transactionService.makeTransaction(playerId, amount, transactionType, transactionId)).thenReturn(transactionDAO);

        mockMvc.perform(post("/transaction/1/credit?amount=200.0&transactionId=abc"))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", aMapWithSize(7)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(200.0)))
                .andExpect(jsonPath("$.type", is("CREDIT")))
                .andExpect(jsonPath("$.amount", is(200.0)));
    }

    @Test
    void debitTransactionExpectResponseStatusCreated() throws Exception {
        Long playerId = 1L;
        Double amount = 200.0;
        TransactionType transactionType = TransactionType.DEBIT;
        String transactionId = "abc";

        CurrencyDAO currencyDAO = new CurrencyDAO(1L, "Euro", "EUR", "€");
        BalanceDAO balanceDAO = new BalanceDAO(1L, 0.0, currencyDAO);
        PlayerDAO playerDAO = new PlayerDAO(1L, "Jane", "Doe", "jane.doe@mail.com", balanceDAO);
        TransactionDAO transactionDAO = new TransactionDAO(1L, transactionId, 0.0, "DEBIT", amount, playerDAO, currencyDAO);

        when(transactionService.makeTransaction(playerId, amount, transactionType, transactionId)).thenReturn(transactionDAO);

        mockMvc.perform(post("/transaction/1/debit?amount=200.0&transactionId=abc"))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", aMapWithSize(7)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(0.0)))
                .andExpect(jsonPath("$.type", is("DEBIT")))
                .andExpect(jsonPath("$.amount", is(200.0)));
    }

}
