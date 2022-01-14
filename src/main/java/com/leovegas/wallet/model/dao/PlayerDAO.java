package com.leovegas.wallet.model.dao;


public class PlayerDAO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private BalanceDAO balance;

    public PlayerDAO() {
    }

    public PlayerDAO(Long id, String firstName, String lastName, String email, BalanceDAO balance) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BalanceDAO getBalance() {
        return balance;
    }

    public void setBalance(BalanceDAO balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                '}';
    }
}
