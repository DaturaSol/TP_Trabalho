package trabalho.candidatura.model;

public class Pessoa {
    protected String nome;
    protected String cpfCnpj;
    protected String email;
    protected String endereco;
    protected long telefone;

    // A no-argument constructor is often needed by JSON libraries
    public Pessoa() {
    }

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

    

    @Override
    public String toString() {
        return "Pessoa{" +
                "cpfCnpj='" + cpfCnpj + "\'" +
                ", nome='" + nome + "\'" +
                ", email='" + email + "\'" +
                ", endereco='" + endereco + "\'" +
                ", telefone=" + telefone +
                "}";
    }

}
