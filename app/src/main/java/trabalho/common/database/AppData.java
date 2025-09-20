package trabalho.common.database;

import trabalho.admin.model.Usuario;
// Import other model classes here as they are created
// import trabalho.candidatura.model.Candidato;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class that holds all the data for the application.
 * This entire object will be serialized to and from a single JSON file.
 */
public class AppData {
    private List<Usuario> usuarios;
    // private List<Candidato> candidatos;
    // private List<Vaga> vagas;
    // ... add lists for all other data types

    public AppData() {
        usuarios = new ArrayList<>();
        // candidatos = new ArrayList<>();
        // vagas = new ArrayList<>();
    }

    // Getters are essential for Gson to read the data
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    // Add getters for other lists
}