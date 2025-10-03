package trabalho.admin.model;

import java.util.HashSet;
import java.util.Set;

import trabalho.common.model.Role;
import trabalho.candidatura.model.Pessoa;

public class Usuario extends Pessoa {

    protected String login;
    protected String passHash;
    // Good idea to use Sets here to keep the uniquines of a role.
    protected Set<Role> roles = new HashSet<>(); 

    // A no-argument constructor is often needed by JSON libraries
    public Usuario() {
    }

    public Usuario(
        String nome,
        String cpfCnpj,
        String email,
        String endereco,
        long telefone,
        String login,
        String passHash,
        Role role) {
        super(nome,
                cpfCnpj,
                email,
                endereco,
                telefone);
        this.login = login;
        this.passHash = passHash;
        roles.add(role);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void dellRole(Role role) {
        roles.remove(role);
    }

    // TODO: Verify if roles parses properly
    @Override
    public String toString() {
        return "Usuario{" +
                "cpfCnpj='" + super.cpfCnpj + '\'' +
                ", login='" + login + '\'' +
                ", role=" + roles +
                '}';
    }
}