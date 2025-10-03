package trabalho.admin.model;

import java.util.ArrayList;
import java.util.List;

import trabalho.common.model.Role;

public class Usuario {

    // We will need a way to generate unique IDs
    private long id;
    private String username;
    private String passwordHash; // In a real app, this would be a hash, not plain text
    private List<Role> roles = new ArrayList<>();

    // A no-argument constructor is often needed by JSON libraries
    public Usuario() {
    }

    public Usuario(long id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles.add(role);
    }

    // Getters and Setters are crucial for the JSON library to access the fields
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + roles +
                '}';
    }
}