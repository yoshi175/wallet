package com.leovegas.wallet.model.dto;

import com.leovegas.wallet.model.constant.Currency;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class PlayerDTO {

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(regexp = ".+[@].+\\..+", message = "Requires an email format.")
    private String email;

    private Currency currency;

    public PlayerDTO() {
    }

    public PlayerDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public PlayerDTO(String firstName, String lastName, String email, Currency currency) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.currency = currency;
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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
