package com.leovegas.wallet.service;

import com.leovegas.wallet.exception.InternalLogicException;
import com.leovegas.wallet.exception.JoinEntitiesFailedException;
import com.leovegas.wallet.exception.PlayerNotExistException;
import com.leovegas.wallet.exception.TransactionFailedException;
import com.leovegas.wallet.model.constant.TransactionType;
import com.leovegas.wallet.model.dao.TransactionDAO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.model.entity.Transaction;
import com.leovegas.wallet.model.util.DAOMapper;
import com.leovegas.wallet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    PlayerService playerService;

    public List<TransactionDAO> findAllByPlayerId(Long playerId) {
        log.info(String.format("Fetching all transactions by player id %d.", playerId));
        return transactionRepository.findAllByPlayerId(playerId).stream().map(DAOMapper::transactionToTransactionDAO).collect(Collectors.toList());
    }

    /**
     * Goes through with a transaction, credit or debit. If any constraints are violated, then a rollback is initiated.
     *
     * @param playerId        is the id of the player who has requested the transaction.
     * @param amount          is the desired value to be credited or debited.
     * @param transactionType is the type of transaction to be made, either credit or debit.
     * @return the new transaction record.
     */
    @Transactional
    public TransactionDAO makeTransaction(Long playerId, Double amount, TransactionType transactionType, String transactionId) {
        if (playerId == null || amount == null || transactionType == null || transactionId == null) {
            log.warn(String.format("Insufficient input data to make a transaction. One or more of playerId (%d), " +
                            "amount (%f), transactionType (%s) or transactionId (%s) is null", playerId, amount,
                    transactionType != null ? transactionType.toString() : null, transactionId));
            throw new TransactionFailedException("Insufficient input data to make a transaction.");
        }

        log.info(String.format("Initialising a %f %s transaction for player with id %d", amount, transactionType.toString(), playerId));
        Optional<Player> optionalPlayer = playerService.findPlayerById(playerId);
        if (optionalPlayer.isPresent()) {
            // Checking in the database if the new transactionId is free to use or already occupied by another transaction.
            Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(transactionId);
            if (optionalTransaction.isPresent()) {
                String errorMessage = String.format("The requested transaction with 'transactionId' %s is already occupied. Please provide a unique id.", transactionId);
                log.warn(errorMessage);
                throw new TransactionFailedException(errorMessage);
            }

            Player player = optionalPlayer.get();
            if (transactionType.equals(TransactionType.CREDIT))
                creditBalance(player.getBalance(), amount);
            else if (transactionType.equals(TransactionType.DEBIT))
                debitBalance(player.getBalance(), amount);

            // Checking in the database if there is exactly one 'last' transaction record. It is a control check to see
            // that the logic is correct and working as it should for keeping track of the transaction history.
            List<Transaction> lastTransactionInList = transactionRepository.findAllByPlayerIdAndLast(playerId, true);
            if (lastTransactionInList != null && lastTransactionInList.size() == 1) {
                Transaction newTransaction = new Transaction();
                setNewAndLastTransaction(lastTransactionInList.get(0), newTransaction, player, transactionType, amount, transactionId);
                return DAOMapper.transactionToTransactionDAO(transactionRepository.save(newTransaction));
            } else {
                log.error(String.format("Greater or less than one 'last' records in the transaction table for player with id %d", playerId));
                throw new InternalLogicException(String.format(
                        "Greater or less than one 'last' records in the transaction table for player with id %d", playerId)
                );
            }

        } else {
            log.warn(String.format("Could not find a player with id %d", playerId));
            throw new PlayerNotExistException(String.format("Could not find a player with id %d", playerId));
        }
    }

    /**
     * Initializing a transaction history for a newly registered player.
     *
     * @param newPlayer is the newly registered player.
     */
    public void initializeTransactionHistoryForANewPlayer(Player newPlayer) {
        Transaction transaction;
        if (newPlayer != null) {
            transaction = new Transaction();

            // Just adding a unique transaction id to the initial transaction when register a new player.
            // Because in the database I added a constraint in the 'transaction_id' column, 'NOT NULL'.
            // The alternative is to accept null as a value for the column and not instantiate the attribute here.
            transaction.setTransactionId(UUID.randomUUID().toString());

            transaction.setPlayer(newPlayer);
            transaction.setBalance(0d);
            transaction.setAmount(0d);
            transaction.setCurrency(newPlayer.getBalance().getCurrency());
            transaction.setType(TransactionType.INITIAL);
            transaction.setFrom(new Timestamp(System.currentTimeMillis()));
            transaction.setTo(null);
            transaction.setLast(true);
            try {
                transactionRepository.save(transaction);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new JoinEntitiesFailedException("Could not save the initial transaction for a newly registered player, and therefore cannot save the player.", e);
            }
        }
    }

    private void setNewAndLastTransaction(Transaction lastTransaction, Transaction newTransaction,
                                          Player player, TransactionType type, Double amount, String transactionId) {
        newTransaction.setTransactionId(transactionId);
        newTransaction.setPlayer(player);
        newTransaction.setBalance(player.getBalance().getAmount());
        newTransaction.setCurrency(player.getBalance().getCurrency());
        newTransaction.setType(type);
        newTransaction.setAmount(amount);
        newTransaction.setFrom(new Timestamp(System.currentTimeMillis()));
        newTransaction.setTo(null);
        newTransaction.setLast(true);

        lastTransaction.setTo(newTransaction.getFrom());
        lastTransaction.setLast(false);
    }

    private void creditBalance(Balance balance, Double amount) {
        String errorMessage;
        if (balance == null || balance.getAmount() == null) {
            errorMessage = "Invalid balance. Could not proceed with crediting due to the balance and/or the amount is null. Rollback";
            log.error(errorMessage);
            throw new TransactionFailedException(errorMessage);
        }
        if (amount == null || amount <= 0) {
            errorMessage = String.format("Invalid amount. Could not proceed with crediting due to the amount " +
                    "is either null or illegal. Amount is %f. Rollback", amount);
            log.error(errorMessage);
            throw new TransactionFailedException(errorMessage);
        }

        balance.setAmount(balance.getAmount() + amount);
    }

    private void debitBalance(Balance balance, Double amount) {
        String errorMessage;
        if (balance == null || balance.getAmount() == null) {
            errorMessage = "Invalid balance. Could not proceed with debiting due to the balance and/or the amount is null. Rollback";
            log.error(errorMessage);
            throw new TransactionFailedException(errorMessage);
        }
        if (amount == null || amount <= 0) {
            errorMessage = String.format("Invalid amount. Could not proceed with debiting due to the amount " +
                    "is either null or illegal. Amount is %f. Rollback", amount);
            log.error(errorMessage);
            throw new TransactionFailedException(errorMessage);
        }

        balance.setAmount(balance.getAmount() - amount);
        if (balance.getAmount() < 0) {
            errorMessage = "Invalid amount to debit, the amount exceeds the current balance. Could not go through with the transaction, rollback.";
            log.warn(errorMessage);
            throw new TransactionFailedException(errorMessage);
        }
    }

}
