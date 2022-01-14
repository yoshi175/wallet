package com.leovegas.wallet.model.util;

import com.leovegas.wallet.model.dao.BalanceDAO;
import com.leovegas.wallet.model.dao.CurrencyDAO;
import com.leovegas.wallet.model.dao.PlayerDAO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Currency;
import com.leovegas.wallet.model.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DAOMapperTest {

    @Test
    void testMappingACurrencyToACurrencyDAO() {
        Currency currency = new Currency();
        currency.setId(11L);
        currency.setName("US Dollar");
        currency.setCode("USD");
        currency.setSymbol("$");

        CurrencyDAO expectedCurrencyDAO = new CurrencyDAO(11L, "US Dollar", "USD", "$");
        CurrencyDAO actualCurrencyDAO = DAOMapper.currencyToCurrencyDAO(currency);

        Assertions.assertEquals(expectedCurrencyDAO.toString(), actualCurrencyDAO.toString());
    }

    @Test
    void testMappingABalanceToABalanceDAO() {
        Currency currency = new Currency();
        currency.setId(5L);
        currency.setName("British Pound");
        currency.setCode("GBP");
        currency.setSymbol("£");

        Balance balance = new Balance();
        balance.setId(1L);
        balance.setAmount(2339.0);
        balance.setCurrency(currency);

        BalanceDAO expectedBalanceDAO = new BalanceDAO(1L, 2339.0,
                new CurrencyDAO(5L, "British Pound", "GBP", "£"));
        BalanceDAO actualBalanceDAO = DAOMapper.balanceToBalanceDAO(balance);

        Assertions.assertEquals(expectedBalanceDAO.toString(), actualBalanceDAO.toString());
    }

    @Test
    void testMappingAPlayerToAPlayerDAO() {
        Currency currency = new Currency();
        currency.setId(2L);
        currency.setName("Euro");
        currency.setCode("EUR");
        currency.setSymbol("€");

        Balance balance = new Balance();
        balance.setId(63L);
        balance.setAmount(255.0);
        balance.setCurrency(currency);

        Player player = new Player();
        player.setId(63L);
        player.setFirstName("John");
        player.setLastName("Doe");
        player.setEmail("john.doe@mail.com");
        player.setBalance(balance);

        PlayerDAO expectedPlayerDAO = new PlayerDAO(63L, "John", "Doe", "john.doe@mail.com",
                new BalanceDAO(63L, 255.0,
                        new CurrencyDAO(2L, "Euro", "EUR", "€")));
        PlayerDAO actualPlayerDAO = DAOMapper.playerToPlayerDAO(player);

        Assertions.assertEquals(expectedPlayerDAO.toString(), actualPlayerDAO.toString());
    }
}
