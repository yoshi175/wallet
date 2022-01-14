package com.leovegas.wallet.model.dao;

public class TransactionDAO {

    private Long id;
    private String transactionId;
    private Double balance;
    private String type;
    private Double amount;
    private PlayerDAO player;
    private CurrencyDAO currency;

    public TransactionDAO() {
    }

    public TransactionDAO(Long id, String transactionId, Double balance, String type, Double amount, PlayerDAO player, CurrencyDAO currency) {
        this.id = id;
        this.transactionId = transactionId;
        this.balance = balance;
        this.type = type;
        this.amount = amount;
        this.player = player;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PlayerDAO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDAO player) {
        this.player = player;
    }

    public CurrencyDAO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDAO currency) {
        this.currency = currency;
    }
}
