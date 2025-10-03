package trabalho.financeiro.model;

import trabalho.admin.model.Usuario;
import trabalho.common.model.Role;

public class Funcionario extends Usuario {
    protected String cargo;
    protected String status;
    protected String departamento;
    protected double salarioBase;

    // A no-argument constructor is often needed by JSON libraries
    public Funcionario() {

    }

    public Funcionario(
            String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String login,
            String passHash,
            Role role,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {
        super(
                nome,
                cpfCnpj,
                email,
                endereco,
                telefone,
                login,
                passHash,
                role);
        this.cargo = cargo;
        this.status = status;
        this.departamento = departamento;
        this.salarioBase = salarioBase;

    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void consultarContracheques() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Made this little funciton to make it easier to pipe this information
     * in other classes such Administrador, Gestor, Recrutador...
     * 
     * @return
     * 
     *         <pre>
     *         {@code 
     * "cpfCnpj='" + cpfCnpj + '\'' +
                  ", cargo='" + cargo + "\'" +
                  ", status='" + status + "\'" +
                  ", departamento='" + departamento + "\'" +
                  ", salarioBase='" + salarioBase + '\'' 
    }
     * 
     */
    protected String dataString() {
        return "cpfCnpj='" + cpfCnpj + '\'' +
                ", cargo='" + cargo + "\'" +
                ", status='" + status + "\'" +
                ", departamento='" + departamento + "\'" +
                ", salarioBase='" + salarioBase + '\'';
    }

    @Override
    public String toString() {
        return "Funcionario{" + dataString() + "}";
    }
}
