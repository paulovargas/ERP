package com.paulovargas.erpapi.entities;
import java.time.LocalDateTime;

public class BankStatement {
    private Double amount;
    private LocalDateTime timestamp;

    public BankStatement(Double amount, LocalDateTime timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
