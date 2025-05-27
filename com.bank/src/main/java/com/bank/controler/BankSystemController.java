package com.bank.controler;


import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BankSystemController {
    private final AuthController authController;
    private final AccountController accountController;
    private User currentUser;
    private final Scanner scanner;

    public BankSystemController() {
        this.authController = new AuthController();
        this.accountController = new AccountController();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Bem-vindo ao Sistema Bancário Java ===");
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n--- Menu de Login ---");
        System.out.println("1. Login");
        System.out.println("2. Registrar");
        System.out.println("3. Sair");
        System.out.print("Escolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegister();
            case 3 -> {
                System.out.println("Obrigado por usar o Sistema Bancário Java!");
                System.exit(0);
            }
            default -> System.out.println("Ops, essa opção é inválida. Tente novamente!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("Bem-vindo, " + currentUser.getUsername() + "!");
        System.out.println("1. Visualizar Contas");
        System.out.println("2. Depositar Dinheiro");
        System.out.println("3. Sacar Dinheiro");
        System.out.println("4. Transferir Dinheiro");
        System.out.println("5. Ver Histórico de Transações");
        System.out.println("6. Criar Nova Conta");
        System.out.println("7. Logout");
        System.out.print("Escolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> viewAccounts();
            case 2 -> handleDeposit();
            case 3 -> handleWithdraw();
            case 4 -> handleTransfer();
            case 5 -> viewTransactionHistory();
            case 6 -> handleCreateAccount();
            case 7 -> handleLogout();
            default -> System.out.println("Ops, essa opção é inválida. Tente novamente!");
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            Optional<User> userOpt = authController.login(username, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("Login com sucesso!");
            } else {
                System.out.println("Credenciais inválidas.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao Logar: " + e.getMessage());
        }
    }

    private void handleRegister() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            authController.register(username, email, password);
            System.out.println("Registrado com Sucesso! Agora voce pode logar.");
        } catch (Exception e) {
            System.out.println("Erro ao Registrar: " + e.getMessage());
        }
    }

    private void viewAccounts() {
        List<Account> accounts = accountController.getAccountsByUserId(currentUser.getId());
        if (accounts.isEmpty()) {
            System.out.println("Nenhuma conta encontrada. Crie uma nova conta.");
            return;
        }

        System.out.println("\n--- Suas Contas --");
        for (Account account : accounts) {
            System.out.printf("Conta: %s | Saldo: $%.2f%n",
                account.getAccountNumber(), account.getBalance());
        }
    }

    private void handleDeposit() {
        String accountId = selectAccount();
        if (accountId == null) return;

        System.out.print("Digite o valor do Deposito: $");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        
        System.out.print("Digite uma descriçao (opcional): ");
        String description = scanner.nextLine();

        try {
            Transaction transaction = accountController.deposit(accountId, amount, description);
            System.out.println("Depositado com sucesso! ID da Transação: " + transaction.getId());
        } catch (Exception e) {
            System.out.println("Erro ao Depositar: " + e.getMessage());
        }
    }

    private void handleWithdraw() {
        String accountId = selectAccount();
        if (accountId == null) return;

        System.out.print("Digite o valor do Saque: $");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        
        System.out.print("Digite uma descriçao (opcional): ");
        String description = scanner.nextLine();

        try {
            Transaction transaction = accountController.withdraw(accountId, amount, description);
            System.out.println("Sacado com sucesso! ID da Transação: " + transaction.getId());
        } catch (Exception e) {
            System.out.println("Erro ao Sacar: " + e.getMessage());
        }
    }

    private void handleTransfer() {
        String fromAccountId = selectAccount();
        if (fromAccountId == null) return;

        System.out.print("Digite o número da conta de destino para qual deseja transferir: ");
        String targetAccountNumber = scanner.nextLine();
        
        Optional<Account> targetAccountOpt = accountController.getAccountByNumber(targetAccountNumber);
        if (targetAccountOpt.isEmpty()) {
            System.out.println("Não existe uma conta de destino para qual deseja transferir.");
            return;
        }

        System.out.print("Digite o valor da Transferência: $");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        
        System.out.print("Digite uma descriçao (opcional): ");
        String description = scanner.nextLine();

        try {
            accountController.transfer(fromAccountId, targetAccountOpt.get().getId(), amount, description);
            System.out.println("Transferência feito com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao Transferir: " + e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        String accountId = selectAccount();
        if (accountId == null) return;

        List<Transaction> transactions = accountController.getTransactionHistory(accountId);
        if (transactions.isEmpty()) {
            System.out.println("Nenhuma transferência encontrada.");
            return;
        }

        System.out.println("\n--- Histórico de Transações ---");
        for (Transaction transaction : transactions) {
            System.out.printf("%s | %s | $%.2f | %s | %s%n",
                transaction.getTimestamp().toString().substring(0, 19),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTargetAccountId() != null ? "Conta de recebimento: " + transaction.getTargetAccountId() : ""
            );
        }
    }

    private void handleCreateAccount() {
        System.out.print("Digite o número da conta: ");
        String accountNumber = scanner.nextLine();

        try {
            Account newAccount = accountController.createAccount(currentUser.getId(), accountNumber);
            System.out.println("Conta criada com sucesso! Número da conta: " + newAccount.getAccountNumber());
        } catch (Exception e) {
            System.out.println("Erro ao criar uma conta: " + e.getMessage());
        }
    }

    private String selectAccount() {
        List<Account> accounts = accountController.getAccountsByUserId(currentUser.getId());
        if (accounts.isEmpty()) {
            System.out.println("Nenhuma conta encontrada. Cadastre uma primeiro e tente novamente.");
            return null;
        }

        if (accounts.size() == 1) {
            return accounts.get(0).getId();
        }

        System.out.println("\n--- Selecione uma conta ---");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.printf("%d. %s (Saldo: $%.2f)%n",
                i + 1, account.getAccountNumber(), account.getBalance());
        }

        System.out.print("Conta Selecionada (1-" + accounts.size() + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > accounts.size()) {
            System.out.println("Seleção inválida.");
            return null;
        }

        return accounts.get(choice - 1).getId();
    }

    private void handleLogout() {
        authController.logout();
        currentUser = null;
        System.out.println("Deslogado com sucesso!");
    }
}