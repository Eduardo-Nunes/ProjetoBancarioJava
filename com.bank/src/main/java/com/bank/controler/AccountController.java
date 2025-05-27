package com.bank.controler;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.services.AccountDAO;
import com.bank.services.TransactionDAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountController {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AccountController() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public Transaction deposit(String accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser positivo");
        }

        Optional<Account> accountOpt = getAccountById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada");
        }

        Account account = accountOpt.get();
        if (!account.isActive()) {
            throw new IllegalStateException("Essa conta está desativada");
        }

        // Atualiza saldo da conta
        account.deposit(amount);
        accountDAO.save(account);

        // Cria registro da transação
        Transaction transaction = new Transaction(
            UUID.randomUUID().toString(),
            accountId,
            null,
            Transaction.Type.DEPOSIT,
            amount,
            description != null ? description : "Depósito",
            LocalDateTime.now()
        );

        transactionDAO.save(transaction);
        System.out.println("Valor Depósitado com sucesso: " + amount + " na conta " + account.getAccountNumber());
        return transaction;
    }

    public Transaction withdraw(String accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do Saque deve ser positivo");
        }

        Optional<Account> accountOpt = getAccountById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada");
        }

        Account account = accountOpt.get();
        if (!account.isActive()) {
            throw new IllegalStateException("Essa conta está desativada");
        }

        // Verifica se existe saldo suficiente e saca o valor
        account.withdraw(amount);
        accountDAO.save(account);

        // Cria registro da transação
        Transaction transaction = new Transaction(
            UUID.randomUUID().toString(),
            accountId,
            null,
            Transaction.Type.WITHDRAWAL,
            amount,
            description != null ? description : "Saque",
            LocalDateTime.now()
        );

        transactionDAO.save(transaction);
        System.out.println("Valor Sacado com Sucesso: " + amount + " da conta " + account.getAccountNumber());
        return transaction;
    }

    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da Transferência deve ser positiva");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Não é possivel tranferir para a mesma conta");
        }

        Optional<Account> fromAccountOpt = getAccountById(fromAccountId);
        Optional<Account> toAccountOpt = getAccountById(toAccountId);

        if (fromAccountOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta de origem não encontrada");
        }
        if (toAccountOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta de destino não encontrada");
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        if (!fromAccount.isActive() || !toAccount.isActive()) {
            throw new IllegalStateException("Uma das contas está desativada, verifique os dados e tente novamente.");
        }

        // Realiza a transferência
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        // Salva as atualizações nas contas
        accountDAO.save(fromAccount);
        accountDAO.save(toAccount);

        // Dados de registro
        LocalDateTime timestamp = LocalDateTime.now();
        String desc = description != null ? description : "Transferência";

        // Transferência de saida
        Transaction outTransaction = new Transaction(
            UUID.randomUUID().toString(),
            fromAccountId,
            toAccountId,
            Transaction.Type.TRANSFER_OUT,
            amount,
            desc,
            timestamp
        );

        // Transferência de entrada
        Transaction inTransaction = new Transaction(
            UUID.randomUUID().toString(),
            toAccountId,
            fromAccountId,
            Transaction.Type.TRANSFER_IN,
            amount,
            desc,
            timestamp
        );

        transactionDAO.save(outTransaction);
        transactionDAO.save(inTransaction);

        System.out.println("Transferência de valor realizada com sucesso: " + amount + " da conta " + fromAccount.getAccountNumber() +
                          " para a conta " + toAccount.getAccountNumber());
    }

    public Optional<Account> getAccountById(String accountId) {
        return accountDAO.findById(accountId);
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountDAO.findByAccountNumber(accountNumber);
    }

    public List<Account> getAccountsByUserId(String userId) {
        return accountDAO.findByUserId(userId);
    }

    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionDAO.findByAccountIdOrderByTimestampDesc(accountId);
    }

    public BigDecimal getBalance(String accountId) {
        Optional<Account> accountOpt = getAccountById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        return accountOpt.get().getBalance();
    }

    public Account createAccount(String userId, String accountNumber) {
        if (accountDAO.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("Numero de conta já cadastrada na base");
        }

        Account newAccount = new Account(
            UUID.randomUUID().toString(),
            userId,
            accountNumber,
            BigDecimal.ZERO,
            LocalDateTime.now(),
            true
        );

        return accountDAO.save(newAccount);
    }
}