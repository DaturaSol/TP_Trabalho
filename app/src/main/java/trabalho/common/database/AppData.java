package trabalho.common.database;

// Import model classes here as they are created
import trabalho.admin.model.*;
import trabalho.candidatura.model.*;
import trabalho.financeiro.model.*;
// import trabalho.recrutamento.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class that holds all the data for the application.
 * 
 * This entire object will be serialized to and from a single JSON file.
 */
public class AppData {
    private List<Administrador> administradores;
    private List<Gestor> gestores;
    private List<Usuario> usuarios;
    private List<Funcionario> funcionarios;
    private List<Candidato> candidatos;

    public AppData() {
        administradores = new ArrayList<>();
        gestores = new ArrayList<>();
        usuarios = new ArrayList<>();
        funcionarios = new ArrayList<>();
        candidatos = new ArrayList<>();
    }

    // Gson Needs a getter to parse these
    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public List<Gestor> getGestores() {
        return gestores;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}