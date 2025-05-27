package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Account {
    private final String id;
    private final String userId;
    private final String accountNumber;
    private BigDecimal balance;
    private final LocalDateTime createdAt;
    private final boolean active;

    public Account(String id, String userId, String accountNumber, BigDecimal balance, LocalDateTime createdAt, boolean active) {
        this.id = Objects.requireNonNull(id, "ID da Conta não pode ser nula");
        this.userId = Objects.requireNonNull(userId, "ID do Usuário não pode ser nulo");
        this.accountNumber = Objects.requireNonNull(accountNumber, "Numero da Conta não pode ser nulo");
        this.balance = Objects.requireNonNull(balance, "Balance não pode ser nulo");
        this.createdAt = Objects.requireNonNull(createdAt, "Created date não pode ser nulo");
        this.active = active;
        
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance não pode ser negativo");
        }
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount tem que ser positivo");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do Saque tem que ser positivo");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Não pode ser possivel realizar o Saque");
        }
        this.balance = this.balance.subtract(amount);
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", active=" + active +
                '}';
    }
}