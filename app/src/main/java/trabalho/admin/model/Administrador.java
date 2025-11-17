package trabalho.admin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trabalho.exceptions.MissingDataException;
import trabalho.financeiro.model.Funcionario;
import trabalho.common.database.AppData;

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
     * Lazy constructor for Administrador, mainly for testing purposes.
     */
    public Administrador(String cpfCnpj) {
        super(cpfCnpj);
    }

    /**
     * Constructs a new, fully initialized {@code Administrador}.
     *
     * @param cpfCnpj      The administrator's unique CPF identifier.
     * @param cargo        The administrator's job title (e.g., "System
     *                     Administrator").
     * @param status       The administrator's current employment status (e.g.,
     *                     "Active").
     * @param departamento The department the administrator belongs to (e.g., "IT").
     * @param salarioBase  The administrator's base salary.
     * @throws MissingDataException
     */
    public Administrador(
            String cpfCnpj,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {
        super(
                cpfCnpj,
                cargo,
                status,
                departamento,
                salarioBase);
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return This method is intended to return a list of all {@code Usuario}
     *         objects.
     */
    public List<Usuario> listarUsuarios(AppData appData) {
        return appData.getUsuarios().values().stream().toList();
    }

    /**
     * Generates and prints a simple system-wide management report to the console.
     * 
     */
    public Map<String, Integer> gerarRelatorioGestao(AppData appData) {
        int totalAdmins = appData.getAdministradores().size();
        int totalGestores = appData.getGestores().size();
        int totalRecrutadores = appData.getRecrutadores().size();
        int totalFuncionarios = appData.getFuncionarios().size();
        int totalCandidatos = appData.getCandidatos().size();

        Map<String, Integer> relatorio = new HashMap<>();
        relatorio.put("Total de Funcionários", totalFuncionarios);
        relatorio.put("Administradores", totalAdmins);
        relatorio.put("Gestores", totalGestores);
        relatorio.put("Recrutadores", totalRecrutadores);
        relatorio.put("Total de Candidatos", totalCandidatos);
        return relatorio;
    }

}