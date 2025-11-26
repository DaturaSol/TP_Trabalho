package trabalho.recrutamento.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

//Uma Entrevista agendada para uma Candidatura
public class Entrevista {
    
    private String id;
    private LocalDate dataHora;
    private String avaliadorCpf; // Cpf do recrutador
    private Double nota;
    private String parecer;
    private String observacoes;

    //Chave estrangeira para a Candidatura (composta por cpf + vagaId)
    private String candidatoCpf;
    private String vagaId;

    //Construtor para bibliotecas de serialização
    public Entrevista() {
    }

    //Construtor principal para agendar uma nova entrevista
    public Entrevista(LocalDate dataHora, String avaliador, String candidatoCpf, String vagaId) {
        this.id = dataHora.toString();
        this.dataHora = dataHora;
        this.avaliadorCpf = avaliador;
        this.candidatoCpf = candidatoCpf;
        this.vagaId = vagaId;
        this.nota = 0.0;
        this.parecer = "Aguardando realização.";
    }


    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public LocalDate getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDate dataHora) {
        this.dataHora = dataHora;
    }

    public String getAvaliadorCpf() {
        return avaliadorCpf;
    }
    public void setAvaliadorCpf(String avaliador) {
        this.avaliadorCpf = avaliador;
    }

    public Double getNota() {
        return nota;
    }
    public String getParecer() {
        return parecer;
    }

    public String getCandidatoCpf() {
        return candidatoCpf;
    }

    public String getVagaId() {
        return vagaId;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public void setVagaId(String vagaId) {
        this.vagaId = vagaId;
    }

    public void setCandidaturaCpf(String candidatoCpf) {
        this.candidatoCpf = candidatoCpf;
    }

    public void setId(String string) {
        this.id = id;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    //Registra o resultado após a entrevista
    public void registrarResultado(double nota, String parecer) {
        this.nota = nota;
        this.parecer = parecer;
    }

    @Override
    public String toString() {
        return "Entrevista{" +
                "id='" + id + '\'' +
                ", dataHora=" + dataHora +
                ", candidatoCpf='" + candidatoCpf + '\'' +
                ", vagaId='" + vagaId + '\'' +
                ", nota=" + nota +
                '}';
    }

    public void setDataHora(LocalDateTime dataHora2) {
        this.dataHora = dataHora;
    }

}