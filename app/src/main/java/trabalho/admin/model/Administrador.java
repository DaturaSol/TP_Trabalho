package trabalho.admin.model;

import java.util.List;

import trabalho.exceptions.DuplicateDataException;
import trabalho.exceptions.MissingDataException;
import trabalho.financeiro.model.Funcionario;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;

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
            double salarioBase) throws MissingDataException {
        super(
                cpfCnpj,
                cargo,
                status,
                departamento,
                salarioBase);
    }

    /**
     * Register a new user in the system by saving them to the database.
     * 
     * @param usuario The {@code Usuario} object containing the new user's details.
     */
    public void cadastrarUsuario(Usuario usuario) throws DuplicateDataException, MissingDataException {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        appData.addUsuario(usuario);

        // Pushes to memory
        dataManager.saveData();
        System.out.println("Usuário " + usuario.getLogin() + " cadastrado com sucesso.");
    }

    /**
     * Modifies the details of an existing user.
     * This works because savePessoa uses a Map, which will overwrite the
     * entry with the same key (cpfCnpj).
     * 
     * @param usuario The {@code Usuario} object with updated information. The user
     *                to be modified is typically identified by a unique field
     *                like CPF or login within this object.
     */
    public void editarUsuario(Usuario usuario) throws DuplicateDataException, MissingDataException {
        /// Logic is the same {cadastrarUsuario does most of the logic}
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        // LAZY: Remove and re-add.
        appData.removeUsuario(usuario);

        cadastrarUsuario(usuario);

        dataManager.saveData();
        System.out.println("Usuário " + usuario.getLogin() + " editado com sucesso.");
    }

    /**
     * Deletes a user account from the system.
     * 
     * @param usuario The user account to be deleted.
     */
    public void excluirUsuario(Usuario usuario) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        appData.removeUsuario(usuario);

        dataManager.saveData();
        System.out.println("Usuário " + usuario.getLogin() + " excluído com sucesso.");
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return This method is intended to return a list of all {@code Usuario}
     *         objects.
     */
    public List<Usuario> listarUsuarios() {
        return JsonDataManager.getInstance().getData().getUsuarios().values().stream().toList();
    }

    /**
     * Generates and prints a simple system-wide management report to the console.
     * 
     * TODO: Properly modify this once controllers are implemented
     * implemented.
     */
    public void gerarRelatorioGestao() {
        AppData appData = JsonDataManager.getInstance().getData();

        int totalAdmins = appData.getAdministradores().size();
        int totalGestores = appData.getGestores().size();
        int totalRecrutadores = appData.getRecrutadores().size();
        int totalFuncionarios = appData.getFuncionarios().size();
        int totalCandidatos = appData.getCandidatos().size();

        // NOTE: This is temporary, and is not intended to work this way
        System.out.println("--- Relatório de Gestão do Sistema ---");
        System.out.println("Total de Funcionários: " + totalFuncionarios);
        System.out.println("  - Administradores: " + totalAdmins);
        System.out.println("  - Gestores: " + totalGestores);
        System.out.println("  - Recrutadores: " + totalRecrutadores);
        System.out.println("----------------------------------------");
        System.out.println("Total de Candidatos no Sistema: " + totalCandidatos);
        System.out.println("--- Fim do Relatório ---");
    }

}