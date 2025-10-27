package trabalho.common.database;

import trabalho.admin.model.Usuario;
import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.Recrutador;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.recrutamento.model.Vaga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A container class that acts as an in-memory database for the application.
 * Each type of object is stored in its own separate list for simplicity and
 * clarity.
 * This is really dumb, but due to time constrains, we are just gonna store it all
 * and load it.
 * @author Gabriel M.S.O.
 */
public class AppData {
    // Simple list with all registered classes
    private List<Administrador> administradores;
    private List<Gestor> gestores;
    private List<Recrutador> recrutadores;
    private List<Funcionario> funcionarios;
    private List<Candidato> candidatos;
    private List<Vaga> vagas;
    private List<Candidatura> candidaturas;
    // This is overkill, all for a fast login screen...
    // we will never get even close to anything that need
    // this speed.
    // Key: login (String), Value: cpfCnpj (String)
    private transient Map<String, String> loginIndex; // `transient` tells GSON to NOT save this map to the JSON file

    public AppData() {
        this.administradores = new ArrayList<>();
        this.gestores = new ArrayList<>();
        this.recrutadores = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        this.candidatos = new ArrayList<>();
        this.vagas = new ArrayList<>();
        this.candidaturas = new ArrayList<>();

        this.loginIndex = new ConcurrentHashMap<>();
    }

    // --- Start Getters ---
    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public List<Gestor> getGestores() {
        return gestores;
    }

    public List<Recrutador> getRecrutadores() {
        return recrutadores;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    public List<Vaga> getVagas() {
        return vagas;
    }

    public List<Candidatura> getCandidaturas() {
        return candidaturas;
    }
    // --- End Getters ---

    // --- Start Setters ---
    public void saveAdministrador(Administrador a) {
        administradores.add(a);
        updateLoginIndex(a);
    }

    public void saveGestor(Gestor g) {
        gestores.add(g);
        updateLoginIndex(g);
    }

    public void saveRecrutador(Recrutador r) {
        recrutadores.add(r);
        updateLoginIndex(r);
    }

    public void saveFuncionario(Funcionario f) {
        funcionarios.add(f);
        updateLoginIndex(f);
    }

    public void saveCandidato(Candidato c) {
        candidatos.add(c);
    }

    public void saveVaga(Vaga v) {
        vagas.add(v);
    }

    public void saveCandidatura(Candidatura c) {
        candidaturas.add(c);
    }
    // --- End Setters ---

    // --- Start Removers ---
    public void removeAdministrador(Administrador a) {
        administradores.remove(a);
        loginIndex.remove(a.getLogin());
    }

    public void removeGestor(Gestor g) {
        gestores.remove(g);
        loginIndex.remove(g.getLogin());
    }

    public void removeRecrutador(Recrutador r) {
        recrutadores.remove(r);
        loginIndex.remove(r.getLogin());
    }

    public void removeFuncionario(Funcionario f) {
        funcionarios.remove(f);
        loginIndex.remove(f.getLogin());
    }

    public void removeCandidato(Candidato c) {
        candidatos.remove(c);
    }

    public void removeVaga(Vaga v) {
        vagas.remove(v);
    }

    public void removeCandidatura(Candidatura c) {
        candidaturas.remove(c);
    }
    // --- End Removers ---

    // --- Utils ---

    /**
     * Rebuilds the login index from all user lists.
     * MUST be called after loading data from JSON.
     */
    public void rebuildIndexes() {
        if (loginIndex == null) {
            loginIndex = new ConcurrentHashMap<>();
        }
        loginIndex.clear();
        Stream.of(funcionarios, recrutadores, gestores, administradores)
                .flatMap(List::stream) // Combines all lists into one stream of users
                .forEach(this::updateLoginIndex);
        System.out.println("Login index rebuilt. Contains " + loginIndex.size() + " entries.");
    }

    private void updateLoginIndex(Usuario user) {
        if (user != null && user.getLogin() != null) {
            loginIndex.put(user.getLogin(), user.getCpfCnpj());
        }
    }

    /**
     * Finds any user by their login. Scans all relevant lists.
     */
    public Optional<Usuario> findUserByLogin(String login) {
        // This is now slower as we have to search lists.
        // For this project, this is an acceptable trade-off for simplicity.
        return Stream.of(administradores, gestores, recrutadores, funcionarios)
                .flatMap(List::stream)
                .filter(u -> login.equals(u.getLogin()))
                .findFirst()
                .map(u -> (Usuario) u); // Cast to Usuario
    }

    /**
     * Gathers all distinct users from all user-related lists.
     */
    public List<Usuario> getAllUsuarios() {
        List<Usuario> allUsers = new ArrayList<>();
        allUsers.addAll(administradores);
        allUsers.addAll(gestores);
        allUsers.addAll(recrutadores);
        allUsers.addAll(funcionarios);
        // Using a Set to remove duplicates if a person is in multiple lists (e.g.,
        // gestor and funcionario)
        return allUsers.stream().distinct().collect(Collectors.toList());
    }

}