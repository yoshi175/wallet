package com.leovegas.wallet.service;

import com.leovegas.wallet.exception.TransactionFailedException;
import com.leovegas.wallet.model.constant.TransactionType;
import com.leovegas.wallet.model.dao.TransactionDAO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Currency;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.model.entity.Transaction;
import com.leovegas.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class TransactionServiceTest {

    @MockBean
    TransactionRepository transactionRepository;

    @MockBean
    PlayerService playerService;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void expectingASuccessfulCreditTransaction() {
        Long playerId = 1L;
        Double amount = 200.0;
        TransactionType transactionType = TransactionType.CREDIT;
        String transactionId = "abc";

        Currency currency = constructCurrency(1L, "Euro", "EUR", "€");
        Player player = constructPlayer(1L, "Jane", "Doe", "jane.doe@mail.com",
                constructBalance(1L, 700.0, currency));
        Optional<Player> optionalPlayer = Optional.of(player);

        Transaction lastTransaction = constructTransaction(1L, "qwerty", player, 700.0, currency,
                TransactionType.DEBIT, 500.0, new Timestamp(System.currentTimeMillis()), null, true);

        Transaction newTransaction = constructTransaction(2L, transactionId, player, 900.0, currency,
                transactionType, amount, new Timestamp(System.currentTimeMillis()), null, true);

        when(playerService.findPlayerById(1L)).thenReturn(optionalPlayer);
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());
        when(transactionRepository.findAllByPlayerIdAndLast(1L, true)).thenReturn(List.of(lastTransaction));
        when(transactionRepository.save(any())).thenReturn(newTransaction);

        TransactionDAO actual = transactionService.makeTransaction(playerId, amount, transactionType, transactionId);

        assertNotNull(actual);
        assertFalse(lastTransaction.isLast());
    }


    @Test
    void expectingAnTransactionFailedExceptionToBeThrownWhenMakingAnInvalidCreditTransactionWithInvalidInputArgument() {
        Long playerId = null;
        Double amount = 200.0;
        TransactionType transactionType = TransactionType.CREDIT;
        String transactionId = "abc";

        Currency currency = constructCurrency(1L, "Euro", "EUR", "€");
        Player player = constructPlayer(1L, "Jane", "Doe", "jane.doe@mail.com",
                constructBalance(1L, 700.0, currency));
        Optional<Player> optionalPlayer = Optional.of(player);

        Transaction lastTransaction = constructTransaction(1L, "qwerty", player, 700.0, currency,
                TransactionType.DEBIT, 500.0, new Timestamp(System.currentTimeMillis()), null, true);

        Transaction newTransaction = constructTransaction(2L, transactionId, player, 900.0, currency,
                transactionType, amount, new Timestamp(System.currentTimeMillis()), null, true);

        when(playerService.findPlayerById(1L)).thenReturn(optionalPlayer);
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());
        when(transactionRepository.findAllByPlayerIdAndLast(1L, true)).thenReturn(List.of(lastTransaction));
        when(transactionRepository.save(any())).thenReturn(newTransaction);


        Exception exception = assertThrows(TransactionFailedException.class, () ->
                transactionService.makeTransaction(playerId, amount, transactionType, transactionId));

        String expectedMessage = "Insufficient input data to make a transaction.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void expectingAnTransactionFailedExceptionToBeThrownWhenMakingAnInvalidDebitTransactionDebitMoreThanTheBalance() {
        Long playerId = 1L;
        Double amount = 1000.0;
        TransactionType transactionType = TransactionType.DEBIT;
        String transactionId = "abc";

        Currency currency = constructCurrency(1L, "Euro", "EUR", "€");
        Player player = constructPlayer(1L, "Jane", "Doe", "jane.doe@mail.com",
                constructBalance(1L, 700.0, currency));
        Optional<Player> optionalPlayer = Optional.of(player);

        when(playerService.findPlayerById(1L)).thenReturn(optionalPlayer);
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());


        Exception exception = assertThrows(TransactionFailedException.class, () ->
                transactionService.makeTransaction(playerId, amount, transactionType, transactionId));
        String expectedMessage = "Invalid amount to debit, the amount exceeds the current balance. Could not go through with the transaction, rollback.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private Currency constructCurrency(Long id, String name, String code, String symbol) {
        Currency currency = new Currency();
        currency.setId(id);
        currency.setName(name);
        currency.setCode(code);
        currency.setSymbol(symbol);
        return currency;
    }

    private Balance constructBalance(Long id, Double amount, Currency currency) {
        Balance balance = new Balance();
        balance.setId(id);
        balance.setAmount(amount);
        balance.setCurrency(currency);
        return balance;
    }

    private Player constructPlayer(Long id, String firstName, String lastName, String email, Balance balance) {
        Player player = new Player();
        player.setId(id);
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setEmail(email);
        player.setBalance(balance);
        return player;
    }

    private Transaction constructTransaction(Long id, String transactionId, Player player, Double balance, Currency currency,
                                             TransactionType type, Double amount, Timestamp from, Timestamp to, boolean last) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setTransactionId(transactionId);
        transaction.setPlayer(player);
        transaction.setBalance(balance);
        transaction.setCurrency(currency);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setLast(last);
        return transaction;
    }

}
