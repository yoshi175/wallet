package com.leovegas.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leovegas.wallet.model.constant.Currency;
import com.leovegas.wallet.model.dto.PlayerDTO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.repository.PlayerRepository;
import com.leovegas.wallet.service.PlayerService;
import com.leovegas.wallet.web.controller.PlayerController;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlayerController.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:walletdb",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private PlayerRepository playerRepository;

    @Test
    @Order(1)
    void onCreatePlayerWithValidInputExpectResponseCode201Created() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO("Jane", "Doe", "jane.doe@mail.com");
        com.leovegas.wallet.model.entity.Currency currency = constructCurrency(1L, "Euro", "EUR", "€");
        Balance balance = constructBalance(1L, 2500.0, currency);
        Player player = constructPlayer(1L, "Jane", "Doe", "jane.doe@mail.com", balance);

        when(playerService.createPlayer(any())).thenReturn(Optional.of(player));

        mockMvc.perform(post("/player/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void onGetAllPlayersExpectResponseStatus200() throws Exception {
        mockMvc.perform(get("/player/all")).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void onGetPlayerByIdExpectResponseStatus200() throws Exception {
        Player player = constructPlayer(7L, "Jane", "Doe", "jane.doe@mail.com",
                constructBalance(7L, 140.0, constructCurrency(3L, "Euro", "EUR", "€")));

        when(playerService.findPlayerById(7L)).thenReturn(Optional.of(player));

        mockMvc.perform(get("/player/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(5)))
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("jane.doe@mail.com")))
                .andExpect(jsonPath("$.balance", aMapWithSize(3)))
                .andExpect(jsonPath("$.balance.id", is(7)))
                .andExpect(jsonPath("$.balance.amount", is(140.0)))
                .andExpect(jsonPath("$.balance.currency", aMapWithSize(4)))
                .andExpect(jsonPath("$.balance.currency.id", is(3)))
                .andExpect(jsonPath("$.balance.currency.name", is("Euro")))
                .andExpect(jsonPath("$.balance.currency.code", is("EUR")))
                .andExpect(jsonPath("$.balance.currency.symbol", is("€")));
    }

    @Test
    @Order(4)
    void onCreatePlayerWithInvalidInputExpectResponseCode400BadRequest() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO(null, "Doe", "jone.doe@mail.com", Currency.EUR);
        mockMvc.perform(post("/player/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isBadRequest());
    }

    private com.leovegas.wallet.model.entity.Currency constructCurrency(Long id, String name, String code, String symbol) {
        com.leovegas.wallet.model.entity.Currency currency = new com.leovegas.wallet.model.entity.Currency();
        currency.setId(id);
        currency.setName(name);
        currency.setCode(code);
        currency.setSymbol(symbol);
        return currency;
    }

    private Balance constructBalance(Long id, Double amount, com.leovegas.wallet.model.entity.Currency currency) {
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
