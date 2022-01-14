package com.leovegas.wallet.service;

import com.leovegas.wallet.exception.PlayerAlreadyExistException;
import com.leovegas.wallet.model.dto.PlayerDTO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Currency;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.repository.PlayerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class PlayerServiceTest {

    @MockBean
    PlayerRepository playerRepository;

    @MockBean
    CurrencyService currencyService;

    @MockBean
    TransactionService transactionService;

    @InjectMocks
    PlayerService playerService;

    @Test
    void expectingFineResultWhileCallingFindAllPlayers() {
        List<Player> expectedPlayerList = Arrays.asList(
                constructPlayer(1L, "Jane", "Doe", "jane.doe@mail.com",
                        constructBalance(1L, 5010.0, constructCurrency(1L, "Euro", "EUR", "€"))),
                constructPlayer(2L, "John", "Doe", "john.doe@mail.com",
                        constructBalance(2L, 890.0, constructCurrency(2L, "US Dollar", "USD", "$")))
        );

        when(playerRepository.findAll()).thenReturn(expectedPlayerList);
        List<Player> actualList = playerService.findAllPlayers();
        assertEquals(actualList.size(), 2);

        assertEquals(actualList.get(0).getId(), 1L);
        assertEquals(actualList.get(0).getFirstName(), "Jane");
        assertEquals(actualList.get(0).getLastName(), "Doe");
        assertEquals(actualList.get(0).getEmail(), "jane.doe@mail.com");
        assertEquals(actualList.get(0).getBalance().getId(), 1L);
        assertEquals(actualList.get(0).getBalance().getCurrency().getId(), 1L);

        assertEquals(actualList.get(1).getId(), 2L);
        assertEquals(actualList.get(1).getFirstName(), "John");
        assertEquals(actualList.get(1).getLastName(), "Doe");
        assertEquals(actualList.get(1).getEmail(), "john.doe@mail.com");
        assertEquals(actualList.get(1).getBalance().getId(), 2L);
        assertEquals(actualList.get(1).getBalance().getCurrency().getId(), 2L);
    }

    @Test
    void expectingToFindAPlayerById() {
        Optional<Player> optionalPlayer = Optional.of(constructPlayer(1L, "Jane", "Doe", "jane.doe@mail.com",
                constructBalance(1L, 5010.0, constructCurrency(1L, "Euro", "EUR", "€"))));
        when(playerRepository.findById(1L)).thenReturn(optionalPlayer);
        Player actual = optionalPlayer.orElse(null);
        Assertions.assertNotNull(actual);

        assertEquals(1L, actual.getId());
        assertEquals("Jane", actual.getFirstName());
        assertEquals("Doe", actual.getLastName());
        assertEquals("jane.doe@mail.com", actual.getEmail());

        assertEquals(1L, actual.getBalance().getId());
        assertEquals(5010.0, actual.getBalance().getAmount());

        assertEquals(1L, actual.getBalance().getCurrency().getId());
        assertEquals("Euro", actual.getBalance().getCurrency().getName());
        assertEquals("EUR", actual.getBalance().getCurrency().getCode());
        assertEquals("€", actual.getBalance().getCurrency().getSymbol());
    }

    @Test
    void expectingNotToFindAPlayerById() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Player> optionalPlayer = playerService.findPlayerById(1L);
        Player actual = optionalPlayer.orElse(null);
        Assertions.assertNull(actual);
    }

    @Test
    void expectingToSuccessfullyCreateANewPlayer() {
        PlayerDTO playerDTO = new PlayerDTO("John", "Doe", "john.doe@mail.com");
        Player player = constructPlayer(1L, "John", "Doe", "john.doe@mail.com",
                constructBalance(1L, 5010.0, constructCurrency(1L, "Euro", "EUR", "€")));
        when(playerRepository.findByEmail(playerDTO.getEmail())).thenReturn(Optional.empty());
        when(playerRepository.save(any())).thenReturn(player);
        when(currencyService.getCurrencyByName(com.leovegas.wallet.model.constant.Currency.EUR)).thenReturn(
                Optional.of(constructCurrency(1L, "Euro", "EUR", "€")));
        Player actual = playerService.createPlayer(playerDTO).orElse(null);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("John", actual.getFirstName());
        assertEquals("Doe", actual.getLastName());
        assertEquals("john.doe@mail.com", actual.getEmail());
        assertNotNull(actual.getBalance());

    }

    @Test
    void expectingToFailToCreateANewPlayer() {
        String email = "john.doe@mail.com";
        PlayerDTO playerDTO = new PlayerDTO("John", "Doe", email);
        Player player = constructPlayer(1L, "John", "Doe", "john.doe@mail.com",
                constructBalance(1L, 5010.0, constructCurrency(1L, "Euro", "EUR", "€")));
        Optional<Player> optionalPlayer = Optional.of(player);

        when(playerRepository.findByEmail(playerDTO.getEmail())).thenReturn(optionalPlayer);

        Exception exception = assertThrows(PlayerAlreadyExistException.class, () -> playerService.createPlayer(playerDTO));
        String expectedMessage = String.format("A player with the '%s' email already exists.", email);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
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

}
