package com.leovegas.wallet.service;

import com.leovegas.wallet.model.entity.Currency;
import com.leovegas.wallet.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    private final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    private CurrencyRepository currencyRepository;

    public Optional<Currency> getCurrencyByName(com.leovegas.wallet.model.constant.Currency currency) {
        log.info(String.format("Fetching currency by name, %s.", currency.getName()));
        return currencyRepository.findByName(currency.getName());
    }

    public List<Currency> saveAll(List<Currency> currencies) {
        log.info("Persisting a list of currencies.");
        return currencyRepository.saveAll(currencies);
    }

}
