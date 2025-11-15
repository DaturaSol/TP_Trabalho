package trabalho.admin.model;

import trabalho.candidatura.model.Pessoa;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.common.model.Role;

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
public class Usuario {

    // Critical change now instead of inhereting from Pessoa it is a composition.
    // But we are only pointing to cpfCnpj here, since when we
    // serealize it to JSON our original reference to Pessoa would be lost.
    private String cpfCpnj;
    private String login;
    private String passHash;

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
     * @param cpfCnpj  The user's unique CPF or CNPJ identifier.
     * @param login    The username for authentication.
     * @param passHash The hashed password for authentication.
     */
    public Usuario(String cpfCnpj, String login, String passHash) {

        this.cpfCpnj = cpfCnpj;
        this.login = login;
        this.passHash = passHash;

    }

    public String getCpfCnpj() {
        return cpfCpnj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCpnj = cpfCnpj;
    }

    public String getLogin() {
        return login;
    }

    public Pessoa getPessoa() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Pessoa pessoa = appData.getPessoas().get(cpfCpnj);
        return pessoa;
    }

    /**
     * Login must be unique for a given person.
     * But it will be present in the database for diferent
     * types of funcionarios.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
}