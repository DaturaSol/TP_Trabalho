package trabalho.recrutamento.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Classe que representa uma solicitação de Contratação.
 * 
 * Responsabilidades:
 * - Armazenar dados da solicitação
 * - Controlar status (PENDENTE/AUTORIZADA/REJEITADA)
 * - Vincular candidato, vaga e recrutador
 * 
 * @author Aluno 3 - Módulo Recrutamento
 * @version 1.0
 */
public class Contratacao implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ===== ENUMS =====
    
    public enum StatusContratacao {
        PENDENTE("Pendente de Autorização"),
        AUTORIZADA("Autorizada"),
        REJEITADA("Rejeitada");
        
        private final String descricao;
        
        StatusContratacao(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
        
        @Override
        public String toString() {
            return descricao;
        }
    }
    
    // ===== ATRIBUTOS =====
    
    private String id; // ID único da contratação
    private String candidatoCpf; // CPF do candidato
    private String vagaId; // ID da vaga
    private String recrutadorCpf; // CPF do recrutador solicitante
    private String gestorCpf; // CPF do gestor autorizador
    private LocalDate dataSolicitacao;
    private LocalDate dataAutorizacao;
    private StatusContratacao status;
    private Vaga.RegimeContratacao regimeContratacao;
    private String observacoes;
    private String motivoRejeicao;
    
    // ===== CONSTRUTORES =====
    
    /**
     * Construtor padrão
     */
    public Contratacao() {
        this.dataSolicitacao = LocalDate.now();
        this.status = StatusContratacao.PENDENTE;
    }
    
    /**
     * Construtor com parâmetros essenciais
     */
    public Contratacao(String id, String candidatoCpf, String vagaId, 
                       String recrutadorCpf, Vaga.RegimeContratacao regime) {
        this();
        this.id = id;
        this.candidatoCpf = candidatoCpf;
        this.vagaId = vagaId;
        this.recrutadorCpf = recrutadorCpf;
        this.regimeContratacao = regime;
    }
    
    // ===== GETTERS E SETTERS =====
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCandidatoCpf() {
        return candidatoCpf;
    }
    
    public void setCandidatoCpf(String candidatoCpf) {
        this.candidatoCpf = candidatoCpf;
    }
    
    public String getVagaId() {
        return vagaId;
    }
    
    public void setVagaId(String vagaId) {
        this.vagaId = vagaId;
    }
    
    public String getRecrutadorCpf() {
        return recrutadorCpf;
    }
    
    public void setRecrutadorCpf(String recrutadorCpf) {
        this.recrutadorCpf = recrutadorCpf;
    }
    
    public String getGestorCpf() {
        return gestorCpf;
    }
    
    public void setGestorCpf(String gestorCpf) {
        this.gestorCpf = gestorCpf;
    }
    
    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }
    
    public void setDataSolicitacao(LocalDate dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
    
    public LocalDate getDataAutorizacao() {
        return dataAutorizacao;
    }
    
    public void setDataAutorizacao(LocalDate dataAutorizacao) {
        this.dataAutorizacao = dataAutorizacao;
    }
    
    public StatusContratacao getStatus() {
        return status;
    }
    
    public void setStatus(StatusContratacao status) {
        this.status = status;
    }
    
    public Vaga.RegimeContratacao getRegimeContratacao() {
        return regimeContratacao;
    }
    
    public void setRegimeContratacao(Vaga.RegimeContratacao regimeContratacao) {
        this.regimeContratacao = regimeContratacao;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }
    
    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
    
    // ===== MÉTODOS DE NEGÓCIO =====
    
    /**
     * Verifica se a contratação está pendente de autorização
     * 
     * @return true se status é PENDENTE
     */
    public boolean isPendente() {
        return this.status == StatusContratacao.PENDENTE;
    }
    
    /**
     * Verifica se a contratação foi autorizada
     * 
     * @return true se status é AUTORIZADA
     */
    public boolean isAutorizada() {
        return this.status == StatusContratacao.AUTORIZADA;
    }
    
    /**
     * Autoriza a contratação
     * 
     * @param gestorCpf CPF do gestor autorizador
     */
    public void autorizar(String gestorCpf) {
        if (this.status != StatusContratacao.PENDENTE) {
            throw new IllegalStateException("Apenas contratações pendentes podem ser autorizadas");
        }
        this.status = StatusContratacao.AUTORIZADA;
        this.gestorCpf = gestorCpf;
        this.dataAutorizacao = LocalDate.now();
    }
    
    /**
     * Rejeita a contratação
     * 
     * @param gestorCpf CPF do gestor
     * @param motivo Motivo da rejeição
     */
    public void rejeitar(String gestorCpf, String motivo) {
        if (this.status != StatusContratacao.PENDENTE) {
            throw new IllegalStateException("Apenas contratações pendentes podem ser rejeitadas");
        }
        this.status = StatusContratacao.REJEITADA;
        this.gestorCpf = gestorCpf;
        this.dataAutorizacao = LocalDate.now();
        this.motivoRejeicao = motivo;
    }
    
    // ===== MÉTODOS HERDADOS =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contratacao)) return false;
        Contratacao that = (Contratacao) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Contratacao[id=%s, candidato=%s, vaga=%s, status=%s, regime=%s]",
                id, candidatoCpf, vagaId, status, regimeContratacao);
    }

}
