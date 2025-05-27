package com.bank.services;


import com.bank.model.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO {
    private final DatabaseConnection dbConnection;

    public TransactionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Optional<Transaction> findById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Transação pelo ID", e);
        }
        return Optional.empty();
    }

    public List<Transaction> findByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Transação pelo by ID da Conta", e);
        }
        return transactions;
    }

    public List<Transaction> findByAccountIdOrderByTimestampDesc(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Transação pelo by ID da Conta ordenado pelo timestamp", e);
        }
        return transactions;
    }

    public Transaction save(Transaction transaction) {
        if (findById(transaction.getId()).isPresent()) {
            return update(transaction);
        } else {
            return insert(transaction);
        }
    }

    private Transaction insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (id, account_id, target_account_id, type, amount, description, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getAccountId());
            stmt.setString(3, transaction.getTargetAccountId());
            stmt.setString(4, transaction.getType().toString());
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setString(6, transaction.getDescription());
            stmt.setString(7, transaction.getTimestamp().toString());
            
            stmt.executeUpdate();
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir uma Transação", e);
        }
    }

    private Transaction update(Transaction transaction) {
        String sql = "UPDATE transactions SET account_id = ?, target_account_id = ?, type = ?, amount = ?, description = ?, timestamp = ? WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTargetAccountId());
            stmt.setString(3, transaction.getType().toString());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getDescription());
            stmt.setString(6, transaction.getTimestamp().toString());
            stmt.setString(7, transaction.getId());
            
            stmt.executeUpdate();
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar uma Transação", e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar uma Transação", e);
        }
    }

    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as Transações", e);
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getString("id"),
            rs.getString("account_id"),
            rs.getString("target_account_id"),
            Transaction.Type.valueOf(rs.getString("type")),
            rs.getBigDecimal("amount"),
            rs.getString("description"),
            LocalDateTime.parse(rs.getString("timestamp"))
        );
    }
}