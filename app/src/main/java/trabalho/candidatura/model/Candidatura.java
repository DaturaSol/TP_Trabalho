package trabalho.candidatura.model;

import java.util.Date;
import java.util.List;

import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.exceptions.DuplicateDataException;
import trabalho.recrutamento.model.Vaga;

public class Candidatura {
    private String cpfCnpjCandidato;
    private Vaga vaga;
    private Date dataCandidatura;
    private StatusCandidatura status;

    public enum StatusCandidatura {
        PENDENTE, EM_ANALISE, APROVADO, REPROVADO
    }

    public Candidatura() {
    }

    public Candidatura(String cpfCnpjCandidato, Vaga vaga, Date dataCandidatura) {
        this.cpfCnpjCandidato = cpfCnpjCandidato;
        this.vaga = vaga;
        this.dataCandidatura = dataCandidatura;
        this.status = StatusCandidatura.PENDENTE;
    }

    public String getCpfCnpjCandidato() {
        return cpfCnpjCandidato;
    }

    public void setCpfCnpjCandidato(String cpfCnpjCandidato) {
        this.cpfCnpjCandidato = cpfCnpjCandidato;
    }

    public Candidato getCandidato() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Candidato candidato = appData.getCandidatos().get(cpfCnpjCandidato);
        return candidato;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public String getStatus() { // Or make it retunr the enum StatusCandidatura
        return status.name(); // Method to return strings from enums
    }

    public StatusCandidatura getStatusEnum() {
        return status;
    }

    public void setStatus(StatusCandidatura status) { // Status must be of the same type
        this.status = status;
    }

    public static boolean cadastrarCandidatura(Candidatura c) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        for (Candidatura candidatura : appData.getCandidaturas()) {
            if (candidatura.getCpfCnpjCandidato().equals(c.getCpfCnpjCandidato()) &&
                    candidatura.getVaga().equals(c.getVaga())) {
                return false;
            }
        }
        try {
            appData.addCandidatura(c);
            dataManager.saveData();
            return true;
        } catch (DuplicateDataException e) {
            return false;
        }
    }

    public void atualizarStatus(StatusCandidatura novoStatus) {
        this.status = novoStatus;
    }

    public static boolean excluirCandidatura(Candidatura c) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        if (c.status != StatusCandidatura.PENDENTE) {
            return false;
        }
        try {
            appData.removeCandidatura(c);
            dataManager.saveData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void listarCandidaturas() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        List<Candidatura> candidaturas = appData.getCandidaturas();

    }
}
