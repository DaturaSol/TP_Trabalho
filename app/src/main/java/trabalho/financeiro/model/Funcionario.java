package trabalho.financeiro.model;

import trabalho.admin.model.Usuario;
import trabalho.common.model.Role;


/**
* Represents an employee (Funcionario) within the organization.
 * <p>
 * This class extends {@link Usuario}, adding employee-specific information such
 * as job title, status, department, and salary. It serves as a base class
 * for more specialized employee types (e.g., Manager, Administrator).
 *
 * @author Gabriel M.S.O.
*/
public class Funcionario extends Usuario {
    protected String cargo;
    protected String status;
    protected String departamento;
    protected double salarioBase;

    /**
     * No-argument constructor required for libraries like GSON.
     * <p>
     * <strong>Warning: For framework use only.</strong> A Usuario created
     * with this constructor is in an incomplete state until its fields are
     * populated. In application code, always use the parameterized constructor
     * to ensure a valid object is created.
     * @author Gabriel M.S.O.
     */
    public Funcionario() {

    }

    /**
     * Constructs a new, fully initialized {@code Funcionario}.
     *
     * @param nome         The full name of the employee.
     * @param cpfCnpj      The employee's unique CPF identifier.
     * @param email        The employee's primary contact email.
     * @param endereco     The employee's physical address.
     * @param telefone     The employee's primary phone number.
     * @param login        The username for the employee's system account.
     * @param passHash     The hashed password for the system account.
     * @param role         The initial role assigned to the employee's user account (e.g., ADMIN, GESTOR, RECRUTADOR).
     * @param cargo        The employee's job title (e.g., "Software Engineer", "HR Analyst").
     * @param status       The current employment status (e.g., "Active", "On Leave").
     * @param departamento The department the employee belongs to (e.g., "Technology", "Finance").
     * @param salarioBase  The employee's base salary, before any deductions or bonuses.
     */
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
     * Generates a partial string representation of the employee's specific data.
     * <p>
     * This protected helper method is designed to be used by the {@link #toString()}
     * method in this class and can be reused by subclasses to build their own
     * string representations.
     *
     * @return A string fragment containing key employee details. The format is:
     * <pre>{@code "cpfCnpj='...', cargo='...', status='...', departamento='...', salarioBase='...'"}</pre>
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
        return this.getClass().getSimpleName() +"{" + // This ensures the class name matches!
            dataString() + "}";
    }
}
