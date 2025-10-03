package trabalho.admin.model;

import java.util.List;

import trabalho.common.model.Role;
import trabalho.financeiro.model.Funcionario;

public class Administrador extends Funcionario {

    // A no-argument constructor is often needed by JSON libraries
    public Administrador() {

    }

    public Administrador(String nome,
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

    public void cadastrarUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    public void editarUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    public void excluirUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    public List<Usuario> listarUsuarios() {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    public void gerarRelatorioGestao() {
        throw new UnsupportedOperationException("Funcion not Implemented");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() // This ensures the class name matches!
            + "{" + super.dataString() +"}";
    }
}
