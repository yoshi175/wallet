package com.leovegas.wallet.repository;

import com.leovegas.wallet.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Optional<Transaction> findByTransactionId(String transactionId);
    List<Transaction> findAllByPlayerId(Long playerId);
    List<Transaction> findAllByPlayerIdAndLast(Long playerId, boolean last);

}
