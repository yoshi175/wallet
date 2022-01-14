package com.leovegas.wallet.model.util;

import com.leovegas.wallet.model.dao.BalanceDAO;
import com.leovegas.wallet.model.dao.CurrencyDAO;
import com.leovegas.wallet.model.dao.PlayerDAO;
import com.leovegas.wallet.model.dao.TransactionDAO;
import com.leovegas.wallet.model.entity.Balance;
import com.leovegas.wallet.model.entity.Currency;
import com.leovegas.wallet.model.entity.Player;
import com.leovegas.wallet.model.entity.Transaction;

/**
 * Helper class, to map database entity objects to DAO (Data Access Object) objects.
 */
public class DAOMapper {

    /**
     * Mapping a 'Currency' database entity to a 'CurrencyDAO' object.
     *
     * @param currency the database entity.
     * @return the mapped 'CurrencyDAO' object.
     */
    public static CurrencyDAO currencyToCurrencyDAO(Currency currency) {
        if (currency == null)
            return null;
        return new CurrencyDAO(currency.getId(), currency.getName(), currency.getCode(), currency.getSymbol());
    }

    /**
     * Mapping a 'Balance' database entity to a 'BalanceDAO' object.
     *
     * @param balance the database entity.
     * @return the mapped 'BalanceDAO' object.
     */
    public static BalanceDAO balanceToBalanceDAO(Balance balance) {
        if (balance == null)
            return null;
        return new BalanceDAO(balance.getId(), balance.getAmount(), currencyToCurrencyDAO(balance.getCurrency()));
    }

    /**
     * Mapping a 'Player' database entity to a 'PlayerDAO' object.
     *
     * @param player the database entity.
     * @return the mapped 'PlayerDAO' object.
     */
    public static PlayerDAO playerToPlayerDAO(Player player) {
        if (player == null)
            return null;
        return new PlayerDAO(player.getId(), player.getFirstName(), player.getLastName(), player.getEmail(),
                balanceToBalanceDAO(player.getBalance()));
    }

    /**
     * Mapping a 'Transaction' database entity to a 'TransactionDAO' object.
     *
     * @param transaction the database entity.
     * @return the mapped 'TransactionDAO' object.
     */
    public static TransactionDAO transactionToTransactionDAO(Transaction transaction) {
        if (transaction == null)
            return null;
        return new TransactionDAO(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getBalance(),
                transaction.getType() != null ? transaction.getType().toString() : null,
                transaction.getAmount(),
                playerToPlayerDAO(transaction.getPlayer()),
                currencyToCurrencyDAO(transaction.getCurrency()));
    }
}
