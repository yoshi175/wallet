package com.leovegas.wallet.web.controller;

import com.leovegas.wallet.model.dto.PlayerDTO;
import com.leovegas.wallet.model.entity.Currency;
import com.leovegas.wallet.service.CurrencyService;
import com.leovegas.wallet.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping
public class SampleDataPopulationController {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    PlayerService playerService;

    @PostMapping(path = "/populate-the-h2-database-with-sample-data")
    public ResponseEntity<String> populateTheDatabaseWithSampleData() {
        try {
            currencyService.saveAll(Arrays.asList(
                    new Currency("Swedish Krona", "SEK", ":-"),
                    new Currency("Euro", "EUR", "€"),
                    new Currency("US Dollar", "USD", "$")));

            playerService.createPlayer(new PlayerDTO("Charlie", "Anderson", "charlie.andersson@mail.com", com.leovegas.wallet.model.constant.Currency.EUR));
            playerService.createPlayer(new PlayerDTO("Cameron", "Smith", "cameron.smith@mail.com", com.leovegas.wallet.model.constant.Currency.GBP));
            playerService.createPlayer(new PlayerDTO("Adrian", "Walker", "adrian.walker@mail.com", com.leovegas.wallet.model.constant.Currency.USD));
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Data can only be populated once per database flush.", HttpStatus.ALREADY_REPORTED);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = "/add-currencies")
    public ResponseEntity<String> createCurrency() {
        try {
            currencyService.saveAll(Arrays.asList(
                    new Currency("Swedish Krona", "SEK", ":-"),
                    new Currency("Euro", "EUR", "€"),
                    new Currency("US Dollar", "USD", "$")));
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Data can only be populated once per database flush.", HttpStatus.ALREADY_REPORTED);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
