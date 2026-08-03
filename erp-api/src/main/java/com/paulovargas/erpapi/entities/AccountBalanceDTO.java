package com.paulovargas.erpapi.entities;

public class AccountBalanceDTO {

    private Integer accountId;
    private String numberAccount;
    private Double balance;

    public AccountBalanceDTO(Integer accountId, String numberAccount, Double balance) {
        this.accountId = accountId;
        this.numberAccount = numberAccount;
        this.balance = balance;
    }

    public String getNumberAccount() {
        return numberAccount;
    }

    public void setNumberAccount(String numberAccount) {
        this.numberAccount = numberAccount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}

