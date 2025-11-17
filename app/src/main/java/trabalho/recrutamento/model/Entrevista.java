package trabalho.recrutamento.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


public class Entrevista implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ===== ATRIBUTOS =====
    
    private String id; // ID único da entrevista
    private String candidaturaCpf; // CPF do candidato
    private String vagaId; // ID da vaga
    private LocalDateTime dataHora;
    private String avaliador;
    private Double nota; // Nota de 0 a 10
    private String observacoes;
    private boolean realizada;
    
    // ===== CONSTRUTORES =====
    
    /**
     * Construtor padrão
     */
    public Entrevista() {
        this.realizada = false;
    }
    
    /**
     * Construtor com parâmetros essenciais
     */
    public Entrevista(String id, String candidaturaCpf, String vagaId, 
                      LocalDateTime dataHora, String avaliador) {
        this();
        this.id = id;
        this.candidaturaCpf = candidaturaCpf;
        this.vagaId = vagaId;
        this.dataHora = dataHora;
        this.avaliador = avaliador;
    }
    
    // ===== GETTERS E SETTERS =====

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCandidaturaCpf() {
        return candidaturaCpf;
    }
    
    public void setCandidaturaCpf(String candidaturaCpf) {
        this.candidaturaCpf = candidaturaCpf;
    }
    
    public String getVagaId() {
        return vagaId;
    }
    
    public void setVagaId(String vagaId) {
        this.vagaId = vagaId;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    
    public String getAvaliador() {
        return avaliador;
    }
    
    public void setAvaliador(String avaliador) {
        this.avaliador = avaliador;
    }
    
    public Double getNota() {
        return nota;
    }
    
    public void setNota(Double nota) {
        if (nota != null && (nota < 0 || nota > 10)) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        this.nota = nota;
        if (nota != null) {
            this.realizada = true;
        }
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public boolean isRealizada() {
        return realizada;
    }
    
    public void setRealizada(boolean realizada) {
        this.realizada = realizada;
    }
    
    // ===== MÉTODOS DE NEGÓCIO =====
    
    /**
     * Verifica se o candidato foi aprovado na entrevista
     * Critério: nota >= 7.0
     * 
     * @return true se aprovado
     */
    public boolean candidatoAprovado() {
        return nota != null && nota >= 7.0;
    }
    
    /**
     * Verifica se a entrevista já aconteceu
     * 
     * @return true se realizada
     */
    public boolean jaFoiRealizada() {
        return realizada && nota != null;
    }
    
    /**
     * Registra a avaliação da entrevista
     * 
     * @param nota Nota de 0 a 10
     * @param observacoes Observações do avaliador
     */
    public void registrarAvaliacao(double nota, String observacoes) {
        setNota(nota);
        this.observacoes = observacoes;
        this.realizada = true;
    }
    
    // ===== MÉTODOS HERDADOS =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entrevista)) return false;
        Entrevista that = (Entrevista) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Entrevista[id=%s, candidato=%s, vaga=%s, data=%s, nota=%s]",
                id, candidaturaCpf, vagaId, dataHora, nota != null ? nota : "Pendente");
    }

    public static boolean cadastrarEntrevista(Entrevista entrevista) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cadastrarEntrevista'");
    }
}
