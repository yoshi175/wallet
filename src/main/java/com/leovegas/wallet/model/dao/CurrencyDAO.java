package com.leovegas.wallet.model.dao;

public class CurrencyDAO {

    private Long id;
    private String name;
    private String code;
    private String symbol;

    public CurrencyDAO() {
    }

    public CurrencyDAO(Long id, String name, String code, String symbol) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
