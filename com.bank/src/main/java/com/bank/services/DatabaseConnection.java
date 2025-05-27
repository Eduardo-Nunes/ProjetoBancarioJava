package com.bank.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:bank_system.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // Carregando JDBC
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DATABASE_URL);
            initializeTables();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Falha ao conectar-se com o banco de dados", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao obter conexão com o banco de dados", e);
        }
        return connection;
    }

    private void initializeTables() {
        try (Statement stmt = connection.createStatement()) {
            // Cria Tabela do Usuario
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    active INTEGER NOT NULL DEFAULT 1
                )
            """);

            // Cria Tabela de Conta
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    id TEXT PRIMARY KEY,
                    user_id TEXT NOT NULL,
                    account_number TEXT UNIQUE NOT NULL,
                    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                    created_at TEXT NOT NULL,
                    active INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Cria Tabela de Transações
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id TEXT PRIMARY KEY,
                    account_id TEXT NOT NULL,
                    target_account_id TEXT,
                    type TEXT NOT NULL,
                    amount DECIMAL(19,2) NOT NULL,
                    description TEXT,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY (account_id) REFERENCES accounts(id)
                )
            """);

            System.out.println("Tabelas do Banco de Dados criada com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao Criar as Tableas do Bando de Dados", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao finalizar o Banco de Dados: " + e.getMessage());
        }
    }
}