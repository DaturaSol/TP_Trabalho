package trabalho.admin.model;

import java.util.List;

import trabalho.common.model.Role;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.Recrutador;
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
     * Constructs a new, fully initialized {@code Administrador}.
     *
     * @param nome         The full name of the administrator.
     * @param cpfCnpj      The administrator's unique CPF identifier.
     * @param email        The administrator's primary contact email.
     * @param endereco     The administrator's physical address.
     * @param telefone     The administrator's primary phone number.
     * @param login        The username for the administrator's system account.
     * @param passHash     The hashed password for the system account.
     * @param cargo        The administrator's job title (e.g., "System
     *                     Administrator").
     * @param status       The administrator's current employment status (e.g.,
     *                     "Active").
     * @param departamento The department the administrator belongs to (e.g., "IT").
     * @param salarioBase  The administrator's base salary.
     */
    public Administrador(String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String login,
            String passHash,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {
        super(nome,
                cpfCnpj,
                email,
                endereco,
                telefone,
                login,
                passHash,
                Role.ADMIN,
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
    public void cadastrarUsuario(Usuario usuario) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        switch (usuario) {
            case Administrador admin -> appData.saveAdministrador(admin);
            case Gestor gestor -> appData.saveGestor(gestor);
            case Recrutador recrutador -> appData.saveRecrutador(recrutador);
            case Funcionario funcionario -> appData.saveFuncionario(funcionario);
            default -> throw new IllegalArgumentException(
                    "Tipo de usuário desconhecido: " + usuario.getClass().getName());
        }

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
    public void editarUsuario(Usuario usuario) {
        /// Logic is the same {cadastrarUsuario does most of the logic}
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        switch (usuario) {
            case Administrador admin -> {
                appData.getAdministradores().removeIf(a -> a.getCpfCnpj().equals(admin.getCpfCnpj()));
            }
            case Gestor gestor -> {
                appData.getGestores().removeIf(g -> g.getCpfCnpj().equals(gestor.getCpfCnpj()));
            }
            case Recrutador recrutador -> {
                appData.getRecrutadores().removeIf(r -> r.getCpfCnpj().equals(recrutador.getCpfCnpj()));
            }
            case Funcionario funcionario -> {
                appData.getFuncionarios().removeIf(f -> f.getCpfCnpj().equals(funcionario.getCpfCnpj()));
            }
            default -> throw new IllegalArgumentException(
                    "Tipo de usuário desconhecido para edição: " + usuario.getClass().getName());
        }
        cadastrarUsuario(usuario);
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

        switch (usuario) {
            case Administrador admin -> appData.removeAdministrador(admin);
            case Gestor gestor -> appData.removeGestor(gestor);
            case Recrutador recrutador -> appData.removeRecrutador(recrutador);
            case Funcionario funcionario -> appData.removeFuncionario(funcionario);
            default ->
                throw new IllegalArgumentException(
                        "Tipo de usuário desconhecido: " + usuario.getClass().getName());
        }

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
        return JsonDataManager.getInstance().getData().getAllUsuarios();
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

    /**
     * Returns a string representation of the {@code Administrador} object.
     * <p>
     * It reuses the data string format from its superclass, {@link Funcionario},
     * prepending the specific class name 'Administrador'.
     *
     * @return A non-null string representing the administrator's state,
     *         e.g., {@code Administrador{cpfCnpj='...', cargo='...', ...}}.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{" + super.dataString() + "}";
    }
}