package com.leovegas.wallet.model.dao;

public class BalanceDAO {

    private Long id;
    private Double amount;
    private CurrencyDAO currency;

    public BalanceDAO() {
    }

    public BalanceDAO(Long id, Double amount, CurrencyDAO currency) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public CurrencyDAO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDAO currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
