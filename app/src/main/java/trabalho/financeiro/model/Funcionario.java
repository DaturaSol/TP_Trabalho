package trabalho.financeiro.model;

import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Pessoa;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.financeiro.utils.CpfCnpjManager;

/**
 * Represents an employee (Funcionario) within the organization.
 * <p>
 * This class extends {@link Usuario}, adding employee-specific information such
 * as job title, status, department, and salary. It serves as a base class
 * for more specialized employee types (e.g., Manager, Administrator).
 *
 * @author Gabriel M.S.O.
 */
public class Funcionario {
    protected String cargo;
    protected String status;
    protected String departamento;
    protected double salarioBase;
    // Critical change now instead of inhereting from Usuario it is a composition.
    // We will be pointing to the cpfCnpj since it is an unique identifier.
    // For both User and Pessoa.
    protected String cpfCnpj;

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
    public Funcionario() {

    }

    /**
     * Lazy constructor for Funcionario, mainly for testing purposes.
     */
    public Funcionario(String cpfCnpj) {
        this.cpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfCnpj);
    }

    /**
     * Constructs a new, fully initialized {@code Funcionario}.
     *
     * @param cpfCnpj      The employee's unique CPF identifier.
     * @param cargo        The employee's job title (e.g., "Software Engineer", "HR
     *                     Analyst").
     * @param status       The current employment status (e.g., "Active", "On
     *                     Leave").
     * @param departamento The department the employee belongs to (e.g.,
     *                     "Technology", "Finance").
     * @param salarioBase  The employee's base salary, before any deductions or
     *                     bonuses.
     */
    public Funcionario(
            String cpfCnpj,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {

        this.cpfCnpj = cpfCnpj;
        this.cargo = cargo;
        this.status = status;
        this.departamento = departamento;
        this.salarioBase = salarioBase;

    }

    public Pessoa getPessoa() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Pessoa pessoa = appData.getPessoas().get(cpfCnpj);
        return pessoa;
    }

    public Usuario getUsuario() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Usuario usuario = appData.getUsuarios().get(cpfCnpj);
        return usuario;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfCnpj);
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
}
