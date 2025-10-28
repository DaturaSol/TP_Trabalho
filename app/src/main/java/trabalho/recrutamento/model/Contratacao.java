package trabalho.recrutamento.model;

import java.util.Date;
import java.util.UUID;

/**
 * Representa o processo de contratação de um candidato para uma vaga.
 * Inicia com a solicitação do Recrutador e aguarda autorização do Gestor.
 */
public class Contratacao {

    public enum StatusContratacao {
        PENDENTE_AUTORIZACAO,
        AUTORIZADA,
        RECUSADA,
        EFETIVADA // Quando o Financeiro finaliza o cadastro
    }

    private String id;
    private Date dataSolicitacao;
    private Date dataAutorizacao; // Preenchida quando o gestor autoriza/recusa
    private RegimeContratacao regime;
    private StatusContratacao status;

    // Chave estrangeira para a Candidatura
    private String candidatoCpf;
    private String vagaId;

    // Links para os responsáveis
    private String recrutadorSolicitanteCpf;
    private String gestorAutorizanteCpf; // Preenchido pelo Gestor

    // Construtor para bibliotecas de serialização
    public Contratacao() {
        this.id = UUID.randomUUID().toString();
        this.dataSolicitacao = new Date();
        this.status = StatusContratacao.PENDENTE_AUTORIZACAO;
    }

    // Construtor para criar uma nova solicitação de contratação
    public Contratacao(String candidatoCpf, String vagaId, RegimeContratacao regime, String recrutadorSolicitanteCpf) {
        this(); // Chama o construtor padrão
        this.candidatoCpf = candidatoCpf;
        this.vagaId = vagaId;
        this.regime = regime;
        this.recrutadorSolicitanteCpf = recrutadorSolicitanteCpf;
    }

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public Date getDataAutorizacao() {
        return dataAutorizacao;
    }

    public RegimeContratacao getRegime() {
        return regime;
    }
    public void setRegime(RegimeContratacao regime) {
        this.regime = regime;
    }

    public StatusContratacao getStatus() {
        return status;
    }
    public void setStatus(StatusContratacao status) {
        this.status = status;
    }

    public String getCandidatoCpf() {
        return candidatoCpf;
    }

    public String getVagaId() {
        return vagaId;
    }

    public String getRecrutadorSolicitanteCpf() {
        return recrutadorSolicitanteCpf;
    }

    public String getGestorAutorizanteCpf() {
        return gestorAutorizanteCpf;
    }

    // Ação do Gestor para autorizar a contratação
    public void autorizar(String gestorCpf) {
        this.gestorAutorizanteCpf = gestorCpf;
        this.dataAutorizacao = new Date();
        this.status = StatusContratacao.AUTORIZADA;
    }

    // Ação do Gestor para recusar a contratação
    public void recusar(String gestorCpf) {
        this.gestorAutorizanteCpf = gestorCpf;
        this.dataAutorizacao = new Date();
        this.status = StatusContratacao.RECUSADA;
    }
}