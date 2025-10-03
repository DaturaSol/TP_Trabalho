package trabalho.admin.model;

import java.util.List;

import trabalho.candidatura.model.Candidatura;
import trabalho.common.model.Role;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.Contratacao;
import trabalho.recrutamento.model.Recrutador;
import trabalho.recrutamento.model.Vaga;

public class Gestor extends Funcionario {

    // A no-argument constructor is often needed by JSON libraries
    public Gestor() {

    }

    public Gestor(
            String nome,
            String cpfCnpj,
            String email,
            String endereco,
            long telefone,
            String login,
            String passHash,
            Role role,
            String cargo,
            String status,
            String departamento,
            double salarioBase) {
        super(nome,
                cpfCnpj,
                email,
                endereco,
                telefone,
                login,
                passHash,
                role,
                cargo,
                status,
                departamento,
                salarioBase);
    }

    public void criarVaga(Vaga vaga) {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    public void atribuirRecrutador(Vaga vaga, Recrutador recrutador) {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    public void excluirUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Funcion not Implemented");

    }

    public void autorizarContratacao(Contratacao contratacao) {
        throw new UnsupportedOperationException("Funcion not Implemented");

    }

    public List<Candidatura> visualizarCandidaturas() {
        throw new UnsupportedOperationException("Funcion not Implemented");

    }

    @Override
    public String toString() {
        return "Gestor{" + super.dataString() + "}";
    }

}
