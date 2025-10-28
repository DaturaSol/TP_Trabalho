package trabalho.recrutamento.model;

import java.util.Date;
import java.util.UUID;

//Uma Entrevista agendada para uma Candidatura
public class Entrevista {

    private String id;
    private Date dataHora;
    private String avaliador; // Nome do recrutador
    private double nota;
    private String parecer;

    //Chave estrangeira para a Candidatura (composta por cpf + vagaId)
    private String candidatoCpf;
    private String vagaId;

    //Construtor para bibliotecas de serialização
    public Entrevista() {
        this.id = UUID.randomUUID().toString();
    }

    //Construtor principal para agendar uma nova entrevista
    public Entrevista(Date dataHora, String avaliador, String candidatoCpf, String vagaId) {
        this.id = UUID.randomUUID().toString();
        this.dataHora = dataHora;
        this.avaliador = avaliador;
        this.candidatoCpf = candidatoCpf;
        this.vagaId = vagaId;
        this.nota = 0.0;
        this.parecer = "Aguardando realização.";
    }

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public Date getDataHora() {
        return dataHora;
    }
    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getAvaliador() {
        return avaliador;
    }
    public void setAvaliador(String avaliador) {
        this.avaliador = avaliador;
    }

    public double getNota() {
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
                '}';
    }
}