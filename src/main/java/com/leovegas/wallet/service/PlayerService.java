package com.leovegas.wallet.service;

import com.leovegas.wallet.exception.JoinEntitiesFailedException;
import com.leovegas.wallet.exception.PlayerAlreadyExistException;
import com.leovegas.wallet.model.constant.Currency;
import com.leovegas.wallet.model.dto.PlayerDTO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    TransactionService transactionService;

    public List<Player> findAllPlayers() {
        log.info("Fetching all players from the database.");
        return playerRepository.findAll();
    }

    public Optional<Player> findPlayerById(Long id) {
        log.info(String.format("Fetching player with id %d from the database.", id));
        return playerRepository.findById(id);
    }

    /**
     * Successfully creates a new player and persists it into the database, if there is no constraints violated.
     *
     * @param playerDTO a DTO (Data Transfer Object) that contains essential data for creation of a new player.
     * @return the newly created and persisted player.
     */
    @Transactional
    public Optional<Player> createPlayer(PlayerDTO playerDTO) {
        log.info("Will try to create and persist a new player.");
        Player newPlayer = null;
        if (validPlayer(playerDTO)) {
            if (playerRepository.findByEmail(playerDTO.getEmail()).isPresent())
                throw new PlayerAlreadyExistException(String.format("A player with the '%s' email already exists.", playerDTO.getEmail()));
            newPlayer = new Player();
            newPlayer.setFirstName(playerDTO.getFirstName());
            newPlayer.setLastName(playerDTO.getLastName());
            newPlayer.setEmail(playerDTO.getEmail());
            newPlayer.setBalance(getNewBalance(Currency.EUR)); // I hardcoded the currency for now, but further implementation can make it configurable.
            newPlayer = playerRepository.save(newPlayer);
            transactionService.initializeTransactionHistoryForANewPlayer(newPlayer);
            log.info(String.format("Created and persisted a new player with id %d", newPlayer.getId()));
        }
        return Optional.of(newPlayer);
    }

    private Balance getNewBalance(Currency currency) {
        Balance balance = new Balance();
        balance.setAmount(0d);
        balance.setCurrency(currencyService.getCurrencyByName(currency).orElseThrow(() ->
                new JoinEntitiesFailedException(String.format("Could not assign the (%s) currency to the balance.", currency.getName()))));
        return balance;
    }

    private boolean validPlayer(PlayerDTO playerDTO) {
        if (playerDTO == null)
            return false;
        else {
            return playerDTO.getFirstName() != null && playerDTO.getLastName() != null && playerDTO.getEmail() != null;
        }
    }

}
