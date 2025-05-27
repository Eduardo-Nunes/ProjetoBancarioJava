package com.bank;

import com.bank.controler.BankSystemController;
import com.bank.services.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando o Sistema Bancário em Java...");

            // Inicializando conexão com o banco
            DatabaseConnection.getInstance();
            System.out.println("Iniciado banco de dados...");

            // Start na aplicação
            BankSystemController controller = new BankSystemController();
            controller.start();

        } catch (Exception e) {
            System.err.println("Falha ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fechando conexão com o banco
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DatabaseConnection.getInstance().close();
                System.out.println("Conexão com o Banco de dados finalizada.");
            }));
        }
    }
}