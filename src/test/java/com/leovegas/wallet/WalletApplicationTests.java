package com.leovegas.wallet;

import com.leovegas.wallet.exception.PlayerAlreadyExistException;
import com.leovegas.wallet.exception.TransactionFailedException;
import com.leovegas.wallet.model.dao.PlayerDAO;
import com.leovegas.wallet.model.dao.TransactionDAO;
import com.leovegas.wallet.model.dto.PlayerDTO;
import com.leovegas.wallet.web.controller.PlayerController;
import com.leovegas.wallet.web.controller.SampleDataPopulationController;
import com.leovegas.wallet.web.controller.TransactionController;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:walletdb",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.jpa.hibernate.ddl-auto=create-drop"

})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletApplicationTests {

    @Autowired
    PlayerController playerController;

    @Autowired
    TransactionController transactionController;

    @Autowired
    SampleDataPopulationController sampleDataPopulationController;

    @Test
    void contextLoads() {
        assertNotNull(playerController);
        assertNotNull(transactionController);
        assertNotNull(sampleDataPopulationController);
    }

    @Test
    @Order(1)
    public void expectToSuccessfullyCreateANewPlayer() {
        PlayerDTO playerDTO = new PlayerDTO("Jane", "Doe", "jane.doe@mail.com", com.leovegas.wallet.model.constant.Currency.EUR);
        ResponseEntity<PlayerDAO> responseEntity = playerController.createPlayer(playerDTO);

        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getId());
        assertEquals("Jane", responseEntity.getBody().getFirstName());
        assertEquals("Doe", responseEntity.getBody().getLastName());
        assertEquals("jane.doe@mail.com", responseEntity.getBody().getEmail());
    }

    @Test
    @Order(2)
    public void ExpectToFailToCreateANewPlayerDueTooEmailNotUnique() {
        String email = "jane.doe@mail.com";
        PlayerDTO playerDTO = new PlayerDTO("Jane", "Doe", email, com.leovegas.wallet.model.constant.Currency.EUR);

        Exception exception = assertThrows(PlayerAlreadyExistException.class, () -> playerController.createPlayer(playerDTO));
        String expectedMessage = String.format("A player with the '%s' email already exists.", email);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @Order(3)
    public void expectToSuccessfullyPopulateSampleDataForTheFirstTime() {
        sampleDataPopulationController.populateTheDatabaseWithSampleData();
        ResponseEntity<List<PlayerDAO>> responseEntity = playerController.getAllPlayers();
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().size(), 4);
    }

    @Test
    @Order(4)
    public void expectToNotSuccessToPopulateSampleDataForTheSecondTime() {
        ResponseEntity<String> responseEntity = sampleDataPopulationController.populateTheDatabaseWithSampleData();
        String expectedMessage = "Data can only be populated once per database flush.";
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedMessage, responseEntity.getBody());
        assertEquals(208, responseEntity.getStatusCodeValue());

    }

    @Test
    @Order(5)
    public void expectToSuccessfullyCreditAPlayer() {
        ResponseEntity<TransactionDAO> responseEntity = transactionController.creditByPlayerId(1L, 500.0, "ijk");

        String expectedTransactionId = "ijk";
        Double expectedAmount = 500.0;
        Double expectedBalance = 500.0;
        Long playerId = 1L;

        assertNotNull(responseEntity.getBody());
        assertEquals(expectedTransactionId, responseEntity.getBody().getTransactionId());
        assertEquals(expectedBalance, responseEntity.getBody().getBalance());
        assertEquals(expectedAmount, responseEntity.getBody().getAmount());
        assertNotNull(responseEntity.getBody().getPlayer());
        assertEquals(playerId, responseEntity.getBody().getPlayer().getId());
    }

    @Test
    @Order(6)
    public void expectToSuccessfullyDebitAPlayer() {
        ResponseEntity<TransactionDAO> responseEntity = transactionController.debitByPlayerId(1L, 300.0, "xyz");

        String expectedTransactionId = "xyz";
        Double expectedAmount = 300.0;
        Double expectedBalance = 200.0;
        Long playerId = 1L;

        assertNotNull(responseEntity.getBody());
        assertEquals(expectedTransactionId, responseEntity.getBody().getTransactionId());
        assertEquals(expectedBalance, responseEntity.getBody().getBalance());
        assertEquals(expectedAmount, responseEntity.getBody().getAmount());
        assertNotNull(responseEntity.getBody().getPlayer());
        assertEquals(playerId, responseEntity.getBody().getPlayer().getId());
    }

    @Test
    @Order(7)
    public void expectToNotSuccessfullyDebitAPlayer() {
        Exception exception = assertThrows(TransactionFailedException.class, () -> {
            ResponseEntity<TransactionDAO> responseEntity = transactionController.debitByPlayerId(1L, 300.0, "lmn");
            assertEquals(400, responseEntity.getStatusCodeValue());
        });

        String expectedMessage = "Invalid amount to debit, the amount exceeds the current balance. Could not go through with the transaction, rollback.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


}
