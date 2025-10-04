package trabalho.admin.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import trabalho.common.model.Role;
import trabalho.candidatura.model.Pessoa;

/**
 * Represents a user of the application.
 * <p>
 * User data is used for authentication and authorization. An essential
 * invariant
 * of this class is that a {@link Usuario} must always have at least one
 * assigned {@link Role}.
 * Attempts to create a user without a role or remove the last role will result
 * in an exception.
 * <p>
 * We Should not work with this class directly.
 *
 * @author Gabriel M.S.O.
 */
public class Usuario extends Pessoa {

    protected String login;
    protected String passHash;
    // Good idea to use Sets here to keep the uniquines of a role.
    protected Set<Role> roles = new HashSet<>();

    /**
     * No-argument constructor required for libraries like GSON.
     * <p>
     * <strong>Warning: For framework use only.</strong> A Usuario created
     * with this constructor is in an incomplete state until its fields are
     * populated. In application code, always use the parameterized constructor
     * to ensure a valid object is created.
     * 
     * @author Gabriel M.S.O.
     */
    public Usuario() {
    }

    /**
     * Constructs a new {@code Usuario} with all required fields.
     * <p>
     * Ensures the user is created with at least one initial role, satisfying the
     * class invariant.
     *
     * @param nome     The full name of the user.
     * @param cpfCnpj  The user's unique CPF or CNPJ identifier.
     * @param email    The user's primary contact email.
     * @param endereco The user's physical address.
     * @param telefone The user's primary phone number.
     * @param login    The username for authentication.
     * @param passHash The hashed password for authentication.
     * @param initRole The first role to be assigned to the user. Cannot be null.
     */
    public Usuario(
            String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String login,
            String passHash,
            Role initRole) {
        super(nome, cpfCnpj, email, endereco, telefone);
        if (initRole == null) {
            throw new IllegalArgumentException("A user must be created with an initial role.");
        }
        this.login = login;
        this.passHash = passHash;
        roles.add(initRole);
    }

    /**
     * Returns an unmodifiable view of the roles assigned to this user.
     * <p>
     * The returned {@code Set} cannot be modified directly. To change a user's
     * roles,
     * you must use the {@link #addRole(Role)} and {@link #removeRole(Role)}
     * methods.
     * This prevents the roles set from being cleared externally.
     *
     * @return An unmodifiable {@code Set} containing the user's roles.
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
        }
    }

    /**
     * Removes a role from the user.
     * <p>
     * This action is only permitted if the user has more than one role.
     * Attempting to remove the last role will result in an exception, as a user
     * must always have at least one role.
     *
     * @param role The role to remove.
     * @throws IllegalStateException if this is the last role for the user.
     */
    public void removeRole(Role role) {
        if (this.roles.size() <= 1) {
            throw new IllegalStateException("A user must have at least one role. Cannot remove the last role.");
        }
        roles.remove(role);
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

    /**
     * Returns a string representation of the {@code Usuario} object.
     * <p>
     * This representation is primarily intended for logging and debugging purposes.
     * @return A non-null string containing the state of the object.
    */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + // This ensures the class name matches!
                "cpfCnpj='" + super.cpfCnpj + '\'' +
                ", login='" + login + '\'' +
                ", role=" + roles +
                '}';
    }
}