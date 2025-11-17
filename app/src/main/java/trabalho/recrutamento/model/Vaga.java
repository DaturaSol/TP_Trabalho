package trabalho.recrutamento.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Classe que representa uma Vaga de emprego no sistema.
 * 
 * Responsabilidades:
 * - Armazenar informações da vaga
 * - Controlar status (ABERTA/FECHADA)
 * - Gerenciar requisitos e benefícios
 * 
 * @author Aluno 3 - Módulo Recrutamento
 * @version 1.0
 */
public class Vaga implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ===== ENUMS =====
    
    public enum StatusVaga {
        ABERTA("Aberta"),
        FECHADA("Fechada");
        
        private final String descricao;
        
        StatusVaga(String descricao) {
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
    
    public enum RegimeContratacao {
        CLT("CLT"),
        ESTAGIO("Estágio"),
        PJ("Pessoa Jurídica");
        
        private final String descricao;
        
        RegimeContratacao(String descricao) {
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
    
    private String id; // ID único da vaga
    private String cargo;
    private String descricao;
    private String requisitos;
    private double salarioBase;
    private String departamento;
    private StatusVaga status;
    private RegimeContratacao regime;
    private LocalDate dataAbertura;
    private LocalDate dataFechamento;
    private String recrutadorResponsavel; // CPF do recrutador
    private String gestorCriador; // CPF do gestor
    
    // ===== CONSTRUTORES =====
    
    /**
     * Construtor padrão
     */
    public Vaga() {
        this.dataAbertura = LocalDate.now();
        this.status = StatusVaga.ABERTA;
    }
    
    /**
     * Construtor com parâmetros essenciais
     */
    public Vaga(String id, String cargo, double salarioBase, RegimeContratacao regime) {
        this();
        this.id = id;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.regime = regime;
    }
    
    // ===== GETTERS E SETTERS =====
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCargo() {
        return cargo;
    }
    
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getRequisitos() {
        return requisitos;
    }
    
    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }
    
    public double getSalarioBase() {
        return salarioBase;
    }
    
    public void setSalarioBase(double salarioBase) {
        if (salarioBase < 0) {
            throw new IllegalArgumentException("Salário base não pode ser negativo");
        }
        this.salarioBase = salarioBase;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public StatusVaga getStatus() {
        return status;
    }
    
    public void setStatus(StatusVaga status) {
        this.status = status;
    }
    
    public RegimeContratacao getRegime() {
        return regime;
    }
    
    public void setRegime(RegimeContratacao regime) {
        this.regime = regime;
    }
    
    public LocalDate getDataAbertura() {
        return dataAbertura;
    }
    
    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }
    
    public LocalDate getDataFechamento() {
        return dataFechamento;
    }
    
    public void setDataFechamento(LocalDate dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
    
    public String getRecrutadorResponsavel() {
        return recrutadorResponsavel;
    }
    
    public void setRecrutadorResponsavel(String recrutadorResponsavel) {
        this.recrutadorResponsavel = recrutadorResponsavel;
    }
    
    public String getGestorCriador() {
        return gestorCriador;
    }
    
    public void setGestorCriador(String gestorCriador) {
        this.gestorCriador = gestorCriador;
    }
    
    // ===== MÉTODOS DE NEGÓCIO =====
    
    /**
     * Verifica se a vaga está aberta para candidaturas
     * 
     * @return true se status é ABERTA
     */
    public boolean isAberta() {
        return this.status == StatusVaga.ABERTA;
    }
    
    /**
     * Fecha a vaga para novas candidaturas
     */
    public void fechar() {
        this.status = StatusVaga.FECHADA;
        this.dataFechamento = LocalDate.now();
    }
    
    /**
     * Reabre a vaga
     */
    public void reabrir() {
        this.status = StatusVaga.ABERTA;
        this.dataFechamento = null;
    }
    
    // ===== MÉTODOS HERDADOS =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vaga)) return false;
        Vaga vaga = (Vaga) o;
        return Objects.equals(id, vaga.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Vaga[id=%s, cargo=%s, salario=%.2f, status=%s, regime=%s]",
                id, cargo, salarioBase, status, regime);
    }
}
