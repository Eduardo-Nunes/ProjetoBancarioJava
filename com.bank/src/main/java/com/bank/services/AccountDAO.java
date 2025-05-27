package com.bank.services;



import com.bank.model.Account;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAO {
    private final DatabaseConnection dbConnection;

    public AccountDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Optional<Account> findById(String id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Conta pelo ID", e);
        }
        return Optional.empty();
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Conta pelo numero da conta", e);
        }
        return Optional.empty();
    }

    public List<Account> findByUserId(String userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Conta pelo ID do Usuário", e);
        }
        return accounts;
    }

    public Account save(Account account) {
        if (findById(account.getId()).isPresent()) {
            return update(account);
        } else {
            return insert(account);
        }
    }

    private Account insert(Account account) {
        String sql = "INSERT INTO accounts (id, user_id, account_number, balance, created_at, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, account.getId());
            stmt.setString(2, account.getUserId());
            stmt.setString(3, account.getAccountNumber());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.setString(5, account.getCreatedAt().toString());
            stmt.setInt(6, account.isActive() ? 1 : 0);
            
            stmt.executeUpdate();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir uma Conta", e);
        }
    }

    private Account update(Account account) {
        String sql = "UPDATE accounts SET user_id = ?, account_number = ?, balance = ?, active = ? WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, account.getUserId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.setInt(4, account.isActive() ? 1 : 0);
            stmt.setString(5, account.getId());
            
            stmt.executeUpdate();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar uma Conta", e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar uma Conta", e);
        }
    }

    public boolean existsByAccountNumber(String accountNumber) {
        return findByAccountNumber(accountNumber).isPresent();
    }

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as Contas", e);
        }
        return accounts;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(
            rs.getString("id"),
            rs.getString("user_id"),
            rs.getString("account_number"),
            rs.getBigDecimal("balance"),
            LocalDateTime.parse(rs.getString("created_at")),
            rs.getInt("active") == 1
        );
    }
}