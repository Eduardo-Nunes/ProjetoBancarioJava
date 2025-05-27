package com.bank.controler;

import com.bank.model.User;
import com.bank.services.UserDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;


public class AuthController {
    private final UserDAO userDAO;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    public Optional<User> login(String username, String password) {
        try {
            Optional<User> userOpt = userDAO.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                System.out.println("Usuário não encontrado: " + username);
                return Optional.empty();
            }

            User user = userOpt.get();
            
            if (!user.isActive()) {
                throw new IllegalStateException("Esse Usuário foi desativado.");
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword.equals(user.getPasswordHash())) {
                System.out.println("Logado com Sucesso com o Usuario: " + username);
                return Optional.of(user);
            } else {
                System.out.println("Senha inválida para o usuário: " + username);
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("Erro ao Logar: " + e.getMessage());
            throw new RuntimeException("Falha no Login", e);
        }
    }

    public User register(String username, String email, String password) {
        if (userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException("Username já cadastrado na base de dados.");
        }
        
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado na base de dados.");
        }

        String userId = java.util.UUID.randomUUID().toString();
        String hashedPassword = hashPassword(password);
        
        User newUser = new User(
            userId,
            username,
            email,
            hashedPassword,
            java.time.LocalDateTime.now(),
            true
        );

        return userDAO.save(newUser);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error criptografando a senha", e);
        }
    }

    public void logout() {
        System.out.println("Usuário deslogado com sucesso.");
    }
}