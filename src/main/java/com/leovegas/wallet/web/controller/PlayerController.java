package com.leovegas.wallet.web.controller;

import com.leovegas.wallet.exception.PlayerNotExistException;
import com.leovegas.wallet.model.dao.PlayerDAO;
import com.leovegas.wallet.model.dto.PlayerDTO;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.model.util.DAOMapper;
import com.leovegas.wallet.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/player")
public class PlayerController {

    private final Logger log = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private PlayerService playerService;

    @GetMapping(path = "/all")
    public ResponseEntity<List<PlayerDAO>> getAllPlayers() {
        log.info("/player/all GET endpoint called.");
        return new ResponseEntity<>(playerService.findAllPlayers().stream().
                map(DAOMapper::playerToPlayerDAO).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PlayerDAO> getPlayerById(@PathVariable Long id) {
        log.info(String.format("/player/%d GET endpoint called.", id));
        Optional<Player> optionalPlayer = playerService.findPlayerById(id);
        return new ResponseEntity<>(optionalPlayer.map(DAOMapper::playerToPlayerDAO).orElse(null),
                optionalPlayer.isPresent() ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/create")
    @Transactional
    public ResponseEntity<PlayerDAO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        log.info("/player/create POST endpoint called.");
        Optional<Player> optionalNewPlayer = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(optionalNewPlayer.map(DAOMapper::playerToPlayerDAO).orElse(null),
                optionalNewPlayer.isPresent() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/{id}/balance")
    public ResponseEntity<Double> currentBalanceByPlayerId(@PathVariable Long id) {
        log.info(String.format("/player/%d/balance GET endpoint called.", id));
        return new ResponseEntity<>(playerService.findPlayerById(id).orElseThrow(() ->
                        new PlayerNotExistException(String.format("Could not find a player with the id %d", id)))
                .getBalance().getAmount(), HttpStatus.OK);
    }

}
