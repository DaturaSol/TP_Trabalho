package trabalho.candidatura.model;

import java.util.Objects;

/**
 * Represents the base model in which all other objects will inherit from.
 * <p>
 * We should not work with this class directly.
 * 
 * @author Gabriel M.S.O.
*/
public class Pessoa {
    protected String nome;
    protected String cpfCnpj;
    protected String email;
    protected String endereco;
    protected long telefone;

    /**
     * No-argument constructor required for libraries like GSON.
     * <p>
     * <strong>Warning: For framework use only.</strong> A Usuario created
     * with this constructor is in an incomplete state until its fields are
     * populated. In application code, always use the parameterized constructor
     * to ensure a valid object is created.
     * @author Gabriel M.S.O.
     */
    public Pessoa() {

    }

    /**
     * Constructs a new {@code Pessoa} with specified initial values.
     *
     * @param nome      The full name of the person or legal entity.
     * @param cpfCnpj   This should be a CPF (for a natural person)
     *                  or a CNPJ (for a legal entity).
     * @param email     The primary contact email address.
     * @param endereco  The physical or mailing address.
     * @param telefone  The primary contact phone number.
     */
    public Pessoa(
            String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
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

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
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

    /**
     * Returns a string representation of the {@code Pessoa} object.
     * <p>
     * This representation is primarily intended for logging and debugging purposes.
     * @return A non-null string containing the state of the object.
    */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + // This ensures the class name matches!
                "cpfCnpj='" + cpfCnpj + "\'" +
                ", nome='" + nome + "\'" +
                ", email='" + email + "\'" +
                ", endereco='" + endereco + "\'" +
                ", telefone=" + telefone +
                "}";
    }

    /**
     * Compares this Pessoa to the specified object for equality.
     * <p>
     * The result is {@code true} if and only if the argument is not {@code null} and is a
     * {@code Pessoa} object that has the same {@code cpfCnpj} as this object. The comparison
     * is based solely on the {@code cpfCnpj} as it is expected to be a unique identifier.
     *
     * @param o The object to compare this {@code Pessoa} against.
     * @return {@code true} if the given object represents a {@code Pessoa} equivalent to this one,
     *         {@code false} otherwise.
     * 
     * @author Gabriel M.S.O.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(cpfCnpj, pessoa.cpfCnpj);
    }

}
