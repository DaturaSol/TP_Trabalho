package trabalho.candidatura.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import trabalho.recrutamento.model.Vaga;

public class Candidatura {
    private Candidato candidato;
    private Vaga vaga;
    private Date dataCandidatura;
    private StatusCandidatura status;

    private static final List<Candidatura> listaCandidaturas = new ArrayList<>();

    public enum StatusCandidatura {
        PENDENTE, EM_ANALISE, APROVADO, REPROVADO
    }

    public Candidatura() {
    }

    public Candidatura(Candidato candidato, Vaga vaga, Date dataCandidatura) {
        this.candidato = candidato;
        this.vaga = vaga;
        this.dataCandidatura = dataCandidatura;
        this.status = StatusCandidatura.PENDENTE;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
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

    public void setStatus(StatusCandidatura status) { // Status must be of the same type
        this.status = status;
    }

    public static boolean cadastrarCandidatura(Candidatura c) {
        for (Candidatura existente : listaCandidaturas) {
            if (existente.candidato.equals(c.candidato) &&
                    existente.vaga.equals(c.vaga)) {
                return false;
            }
        }
        listaCandidaturas.add(c);
        return true;
    }

    public void atualizarStatus(StatusCandidatura novoStatus) {
        this.status = novoStatus;
    }

    public static boolean excluirCandidatura(Candidatura c) {
        if (c.status != StatusCandidatura.PENDENTE) {
            return false;
        }
        listaCandidaturas.remove(c);
        return true;
    }

    public static void listarCandidaturas() {
        if (listaCandidaturas.isEmpty()) {
            return;
        }
        for (Candidatura c : listaCandidaturas) {
            System.out.println(c);
        }
    }

    @Override
    public String toString() {
        return "Candidatura{" +
                "candidato=" + (candidato != null ? candidato.getNome() : "null") +
                ", vaga=" + (vaga != null ? vaga.getCargo() : "null") +
                ", status='" + status + '\'' +
                '}';
    }
}
