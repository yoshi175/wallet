package com.leovegas.wallet.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "currency")
public class Currency extends AuditEntity {

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    public Currency() {
    }

    public Currency(String name, String code, String symbol) {
        this.name = name;
        this.code = code;
        this.symbol = symbol;
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
}
