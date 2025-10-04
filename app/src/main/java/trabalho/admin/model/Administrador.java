package trabalho.admin.model;

import java.util.List;

import trabalho.common.model.Role;
import trabalho.financeiro.model.Funcionario;

/**
 * Represents a system Administrator.
 * <p>
 * An Administrator holds the highest level of privileges within the system,
 * responsible for core user account management (CRUD operations) and generating
 * system-wide reports. This class extends {@link Funcionario}, as an
 * administrator
 * is also an employee.
 *
 * @author Gabriel M.S.O.
 */
public class Administrador extends Funcionario {

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
    public Administrador() {

    }

    /**
     * Constructs a new, fully initialized {@code Administrador}.
     *
     * @param nome         The full name of the administrator.
     * @param cpfCnpj      The administrator's unique CPF identifier.
     * @param email        The administrator's primary contact email.
     * @param endereco     The administrator's physical address.
     * @param telefone     The administrator's primary phone number.
     * @param login        The username for the administrator's system account.
     * @param passHash     The hashed password for the system account.
     * @param cargo        The administrator's job title (e.g., "System
     *                     Administrator").
     * @param status       The administrator's current employment status (e.g.,
     *                     "Active").
     * @param departamento The department the administrator belongs to (e.g., "IT").
     * @param salarioBase  The administrator's base salary.
     */
    public Administrador(String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String login,
            String passHash,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {
        super(nome,
                cpfCnpj,
                email,
                endereco,
                telefone,
                login,
                passHash,
                Role.ADMIN,
                cargo,
                status,
                departamento,
                salarioBase);
    }

    /**
     * TODO: method for registering a new user in the system.
     *
     * @param usuario The {@code Usuario} object containing the new user's details.
     * @throws UnsupportedOperationException always, as this feature is not yet
     *                                       implemented.
     */
    public void cadastrarUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Function not Implemented");
    }

    /**
     * TODO: method for modifying the details of an existing user.
     *
     * @param usuario The {@code Usuario} object with updated information. The user
     *                to be modified is typically identified by a unique field
     *                like CPF or login within this object.
     * @throws UnsupportedOperationException always, as this feature is not yet
     *                                       implemented.
     */
    public void editarUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Function not Implemented");
    }

    /**
     * TODO: method for deleting a user account from the system.
     *
     * @param usuario The user account to be deleted.
     * @throws UnsupportedOperationException always, as this feature is not yet
     *                                       implemented.
     */
    public void excluirUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Function not Implemented");
    }

    /**
     * TODO: method for retrieving a list of all users in the system.
     *
     * @return This method is intended to return a list of all {@code Usuario}
     *         objects.
     * @throws UnsupportedOperationException always, as this feature is not yet
     *                                       implemented.
     */
    public List<Usuario> listarUsuarios() {
        throw new UnsupportedOperationException("Function not Implemented");
    }

    /**
     * TODO: method for generating a system-wide management report.
     *
     * @throws UnsupportedOperationException always, as this feature is not yet
     *                                       implemented.
     */
    public void gerarRelatorioGestao() {
        throw new UnsupportedOperationException("Function not Implemented");
    }

    /**
     * Returns a string representation of the {@code Administrador} object.
     * <p>
     * It reuses the data string format from its superclass, {@link Funcionario},
     * prepending the specific class name 'Administrador'.
     *
     * @return A non-null string representing the administrator's state,
     *         e.g., {@code Administrador{cpfCnpj='...', cargo='...', ...}}.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{" + super.dataString() + "}";
    }
}