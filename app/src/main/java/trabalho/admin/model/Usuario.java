package trabalho.admin.model;

import trabalho.candidatura.model.Pessoa;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.common.model.Role;
import trabalho.financeiro.utils.CpfCnpjManager;

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
    private String cpfCnpj;
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
    public Usuario(String cpfCnpj, String passHash) {

        this.cpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfCnpj);
        this.passHash = passHash;

    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfCnpj);
    }


    public Pessoa getPessoa() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Pessoa pessoa = appData.getPessoas().get(cpfCnpj);
        return pessoa;
    }


    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
}