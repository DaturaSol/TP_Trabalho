package trabalho.candidatura.model;

import trabalho.financeiro.utils.CpfCnpjManager;

/**
 * Represents the base model in which all other objects will inherit from.
 * <p>
 * We should not work with this class directly.
 *
 * @author Gabriel M.S.O.
 */
public class Pessoa {
    private String cpfCnpj; // Unique identifier for a person.
    private String nome;
    private String email;
    private String endereco;
    private long telefone;

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
    public Pessoa() {

    }

    /**
     * Lazy constructor for Pessoa, mainly for testing purposes.
     */
    public Pessoa(String cpfCnpj) {
        this.cpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfCnpj);
    }

    public Pessoa(String cpfCnpj, String nome) {
        this(cpfCnpj);
        this.nome = nome;
    }

    public Pessoa(String cpfCnpj, String nome, String email) {
        this(cpfCnpj, nome);
        this.email = email;
    }

    /**
     * Constructs a new {@code Pessoa} with specified initial values.
     *
     * @param nome     The full name of the person or legal entity.
     * @param cpfCnpj  This should be a CPF (for a natural person)
     *                 or a CNPJ (for a legal entity).
     * @param email    The primary contact email address.
     * @param endereco The physical or mailing address.
     * @param telefone The primary contact phone number.
     */
    public Pessoa(
            String cpfCnpj,
            String nome,
            String email,
            String endereco,
            long telefone) {
        this(cpfCnpj, nome, email);
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    /**
     * Cpf/Cnpj must be unique for a given person.
     * But it will be present in the database for diferent
     * types of pessoas.
     */
    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfCnpj);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public long getTelefone() {
        return telefone;
    }

    public void setTelefone(long telefone) {
        this.telefone = telefone;
    }
}
