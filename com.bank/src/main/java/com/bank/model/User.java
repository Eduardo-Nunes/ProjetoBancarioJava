package com.bank.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private final String id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final LocalDateTime createdAt;
    private final boolean active;

    public User(String id, String username, String email, String passwordHash, LocalDateTime createdAt, boolean active) {
        this.id = Objects.requireNonNull(id, "ID do Usuário não pode ser nulo");
        this.username = Objects.requireNonNull(username, "Username não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password não pode ser nulo");
        this.createdAt = Objects.requireNonNull(createdAt, "Created date não pode ser nulo");
        this.active = active;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}