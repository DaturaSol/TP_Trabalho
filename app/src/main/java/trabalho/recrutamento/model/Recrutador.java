package trabalho.recrutamento.model;

import trabalho.candidatura.model.Pessoa;
import trabalho.exceptions.MissingDataException;
import trabalho.financeiro.model.Funcionario;

import java.io.Serializable;
import java.util.Objects;


public class Recrutador extends Funcionario {
    
    private static final long serialVersionUID = 1L;
    
    private String matricula;
    private boolean ativo;
    

    public Recrutador() {
        super();
        this.ativo = true;
    }
    

    // public Recrutador(String cpf, String nome, String email, String matricula, double salarioBase) {
    //     super(cpf, nome, email);
    //     this.matricula = matricula;
    //     this.ativo = true;
    // }

        public Recrutador(
            String cpfCnpj,
            String cargo,
            String status,
            String departamento,
            double salarioBase) throws MissingDataException {
        super(
                cpfCnpj,
                cargo,
                status,
                departamento,
                salarioBase);
    }
    
    // ===== GETTERS E SETTERS =====
    
    public String getMatricula() {
        return matricula;
    }
    
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    // ===== MÉTODOS DE NEGÓCIO =====
    
    /**
     * Verifica se o recrutador pode gerenciar processos seletivos
     * 
     * @return true se está ativo
     */
    public boolean podeGerenciarProcessos() {
        return this.ativo;
    }
    
    // ===== MÉTODOS HERDADOS =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recrutador)) return false;
        if (!super.equals(o)) return false;
        Recrutador that = (Recrutador) o;
        return Objects.equals(matricula, that.matricula);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), matricula);
    }
    
    // @Override
    // public String toString() {
    //     return String.format("Recrutador[cpf=%s, nome=%s, matricula=%s, departamento=%s]",
    //             getCpfCnpj(), getPessoa(), matricula, departamento);
    // }
}
