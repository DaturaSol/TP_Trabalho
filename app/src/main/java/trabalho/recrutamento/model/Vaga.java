package trabalho.recrutamento.model;

import java.util.Date;
import java.util.UUID;

/**
 * Representa uma Vaga de emprego.
 * Contém todos os detalhes da vaga e seu status.
 */
public class Vaga {

    // Enum para o status da vaga, conforme o padrão em Candidatura.java
    public enum StatusVaga {
        ABERTA,
        EM_PROCESSO,
        FECHADA
    }

    private String id;
    private String cargo;
    private String departamento;
    private double salarioBase;
    private String requisitos;
    private StatusVaga status;
    private RegimeContratacao regimeContratacao;
    private Date dataAbertura;

    // Links para os responsáveis (usando CPF como ID)
    private String gestorCriadorCpf;
    private String recrutadorResponsavelCpf;

    /**
     * Construtor para bibliotecas de serialização (GSON/JSON).
     * Não use diretamente.
     */
    public Vaga() {
        this.id = UUID.randomUUID().toString();
        this.dataAbertura = new Date();
        this.status = StatusVaga.ABERTA;
    }

    /**
     * Construtor principal para criar uma nova Vaga.
     */
    public Vaga(String cargo, String departamento, double salarioBase, String requisitos,
                RegimeContratacao regime, String gestorCriadorCpf) {
        this.id = UUID.randomUUID().toString();
        this.cargo = cargo;
        this.departamento = departamento;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.regimeContratacao = regime;
        this.gestorCriadorCpf = gestorCriadorCpf;
        this.status = StatusVaga.ABERTA;
        this.dataAbertura = new Date();
    }

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public double getSalarioBase() {
        return salarioBase;
    }
    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public String getRequisitos() {
        return requisitos;
    }
    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public StatusVaga getStatus() {
        return status;
    }
    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public RegimeContratacao getRegimeContratacao() {
        return regimeContratacao;
    }
    public void setRegimeContratacao(RegimeContratacao regimeContratacao) {
        this.regimeContratacao = regimeContratacao;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public String getGestorCriadorCpf() {
        return gestorCriadorCpf;
    }

    public String getRecrutadorResponsavelCpf() {
        return recrutadorResponsavelCpf;
    }
    public void setRecrutadorResponsavelCpf(String recrutadorResponsavelCpf) {
        // Logica de atribuir recrutador
        this.recrutadorResponsavelCpf = recrutadorResponsavelCpf;
        if (this.status == StatusVaga.ABERTA) {
            this.status = StatusVaga.EM_PROCESSO;
        }
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "id='" + id + '\'' +
                ", cargo='" + cargo + '\'' +
                ", status=" + status +
                ", departamento='" + departamento + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vaga vaga = (Vaga) o;
        return id.equals(vaga.id);
    }
}