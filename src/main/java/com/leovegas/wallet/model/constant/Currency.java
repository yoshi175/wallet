package com.leovegas.wallet.model.constant;

public enum Currency {
    EUR("Euro"),
    GBP("British Pound"),
    USD("US Dollar");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
