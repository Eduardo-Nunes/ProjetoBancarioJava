package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
    }

    private final String id;
    private final String accountId;
    private final String targetAccountId; // For transfers
    private final Type type;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(String id, String accountId, String targetAccountId, Type type, 
                      BigDecimal amount, String description, LocalDateTime timestamp) {
        this.id = Objects.requireNonNull(id, "ID da Transação não pode ser nula");
        this.accountId = Objects.requireNonNull(accountId, "ID da Conta não pode ser nula");
        this.targetAccountId = targetAccountId; // Pode ser null
        this.type = Objects.requireNonNull(type, "Tipo de Transaction não pode ser nula");
        this.amount = Objects.requireNonNull(amount, "Amount não pode ser nula");
        this.description = description;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp não pode ser nula");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount tem que ser maior que zero");
        }
    }

    // Getters
    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public Type getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}