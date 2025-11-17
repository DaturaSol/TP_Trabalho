package trabalho.admin.model;

import java.util.List;
import java.util.Map;

import trabalho.common.database.AppData;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.database.JsonDataManager;
import trabalho.exceptions.MissingDataException;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.Contratacao;
import trabalho.recrutamento.model.Recrutador;
import trabalho.recrutamento.model.Vaga;
import trabalho.recrutamento.model.Contratacao.StatusContratacao;

/**
 * Represents a Manager (Gestor) within the organization.
 * <p>
 * A Gestor has elevated privileges related to the hiring process and user
 * management.
 * This class extends {@link Funcionario}, inheriting all base employee
 * attributes
 * and adding management-specific capabilities.
 *
 * @author Gabriel M.S.O.
 */
public class Gestor extends Funcionario {

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
    public Gestor() {

    }

    /**
     * Constructs a new, fully initialized {@code Gestor}.
     *
     * @param cpfCnpj      The manager's unique CPF identifier.
     * @param cargo        The manager's job title (e.g., "Engineering Manager").
     * @param status       The manager's current employment status (e.g., "Active").
     * @param departamento The department the manager oversees (e.g., "Technology").
     * @param salarioBase  The manager's base salary.
     * @throws MissingDataException 
     */
    public Gestor(
            String cpfCnpj,
            String cargo,
            String status,
            String departamento,
            double salarioBase)  {
        super(
                cpfCnpj,
                cargo,
                status,
                departamento,
                salarioBase);
    }

    /**
     * Creates a new job opening (Vaga) and saves it to the system.
     * 
     * @param vaga The {@code Vaga} object containing the details of the job to be
     *             created.
     *             implemented.
     */
    public void criarVaga(Vaga vaga) {

        vaga.setGestorCriadorCpf(cpfCnpj);


    }

    /**
     * Method for assigning a specific recruiter to a job opening.
     *
     * @param vaga       The job opening to which the recruiter will be assigned.
     * @param recrutador The recruiter to be assigned to the job.
     */
    public void atribuirRecrutador(Vaga vaga, Recrutador recrutador) {
        vaga.setRecrutadorResponsavelCpf(recrutador.getCpfCnpj());
    }

    /**
     * Deletes a user account from the system.
     * 
     * @param usuario The user account to be deleted.
     */
    public void excluirUsuario(Usuario usuario) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        Map<String, Administrador> administradoresByCpf = appData.getAdministradores();
        // Manager should not be able to delete a Admin user
        if (administradoresByCpf.containsKey(usuario.getCpfCnpj())) {
            throw new IllegalArgumentException("Um gestor não pode excluir um administrador.");
        }
        appData.removeUsuario(usuario);

        dataManager.saveData();
        System.out.println("Usuário " + usuario.getCpfCnpj() + " excluído com sucesso.");
    }

    /**
     * Method for giving final approval for a hiring decision.
     *
     * @param contratacao The hiring record to be authorized.
     *                    implemented.
     */
    public void autorizarContratacao(Contratacao contratacao) {
        contratacao.setStatus(StatusContratacao.AUTORIZADA);
    }

    /**
     * @return This method is intended to return a list of job applications.
     */
    public List<Candidatura> visualizarCandidaturas() {
        return JsonDataManager.getInstance().getData().getCandidaturas();
    }

}
