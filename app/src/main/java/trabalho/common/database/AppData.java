package trabalho.common.database;

import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Pessoa;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.Recrutador;
import trabalho.common.database.search.FuncionarioSearchCriteria;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A container class that acts as an in-memory database for the application.
 * <p>
 * Stores all {@link Pessoa} objects in a single Map keyed by CPF/CNPJ for O(1)
 * lookups.
 * Provides methods for retrieving specific subtypes and performing
 * multi-criteria searches.
 * 
 * @author Gabriel M.S.O.
 */
public class AppData {
    private Map<String, Pessoa> pessoas;

    // This is overkill, all for a fast login screen...
    // we will never get even close to anything that need
    // this speed.
    // Key: login (String), Value: cpfCnpj (String)
    private transient Map<String, String> loginIndex; // `transient` tells GSON to NOT save this map to the JSON file

    public AppData() {
        this.pessoas = new ConcurrentHashMap<>();
        this.loginIndex = new ConcurrentHashMap<>();
    }

    /**
     * The primary data store. GSON will serialize this map.
     * 
     * @return The map containing all people, keyed by their cpfCnpj.
     */
    public Map<String, Pessoa> getPessoas() {
        return pessoas;
    }

    /**
     * Finds a person by their unique cpfCnpj.
     * 
     * @param cpfCnpj The unique identifier.
     * @return The Pessoa object, or null if not found.
     */
    public Pessoa findPessoaByCpfCnpj(String cpfCnpj) {
        return pessoas.get(cpfCnpj);
    }

    /**
     * Adds or updates a person in the database and updates the login index.
     * @param pessoa The person object to add/update.
     */
    public void savePessoa(Pessoa pessoa) {
        if (pessoa == null || pessoa.getCpfCnpj() == null || pessoa.getCpfCnpj().isBlank()) {
            return;
        }
        Pessoa oldPessoa = pessoas.get(pessoa.getCpfCnpj());
        if (oldPessoa instanceof Usuario oldUser) {
            loginIndex.remove(oldUser.getLogin());
        }

        pessoas.put(pessoa.getCpfCnpj(), pessoa);

        if (pessoa instanceof Usuario newUser) {
            loginIndex.put(newUser.getLogin(), newUser.getCpfCnpj());
        }
    }

    /**
     * Rebuilds all secondary indexes from the primary `pessoas` map.
     * This MUST be called after loading data from JSON.
     */
    public void rebuildIndexes() {
        if (loginIndex == null) {
            loginIndex = new ConcurrentHashMap<>();
        }
        loginIndex.clear();
        for (Pessoa p : pessoas.values()) {
            if (p instanceof Usuario user) {
                if (user.getLogin() != null) {
                    loginIndex.put(user.getLogin(), user.getCpfCnpj());
                }
            }
        }
        System.out.println("Login index rebuilt. Contains " + loginIndex.size() + " entries.");
    }

    /**
     * Finds a user by their login name using the optimized index.
     * This is an O(1) operation.
     *
     * @param login The login name to search for.
     * @return An Optional containing the Usuario if found, otherwise an empty Optional.
     */
    public Optional<Usuario> findUserByLogin(String login) {
        // Step 1: Use the index to get the primary key (cpfCnpj)
        String cpfCnpj = loginIndex.get(login);

        if (cpfCnpj == null) {
            return Optional.empty(); // User not found
        }

        // Step 2: Use the primary key to get the full user object
        Pessoa person = pessoas.get(cpfCnpj);
        
        // Return as an Optional of the correct type
        return Optional.ofNullable((Usuario) person);
    }

    /**
     * Retrieves all users who are Administrators.
     * 
     * @return A List of Administrador objects.
     */
    public List<Administrador> getAllAdministradores() {
        return filterByType(Administrador.class);
    }

    /**
     * Retrieves all users who are Managers.
     * 
     * @return A List of Gestor objects.
     */
    public List<Gestor> getAllGestores() {
        return filterByType(Gestor.class);
    }

    /**
     * Retrieves all users who are Recruiters.
     * 
     * @return A List of Recrutador objects.
     */
    public List<Recrutador> getAllRecrutadores() {
        return filterByType(Recrutador.class);
    }

    /**
     * Retrieves all users who are general employees (Funcionario).
     * This will include Administradores, Gestores, etc., as they are all
     * Funcionarios.
     * 
     * @return A List of all Funcionario objects.
     */
    public List<Funcionario> getAllFuncionarios() {
        return filterByType(Funcionario.class);
    }

    /**
     * Retrieves all people who are Candidates.
     * 
     * @return A List of all Candidato objects.
     */
    public List<Candidato> getAllCandidatos() {
        return filterByType(Candidato.class);
    }

    private <T extends Pessoa> List<T> filterByType(Class<T> type) {
        return pessoas.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    // --- For multiple criteria search --- //
    /**
     * Searches for Funcionarios based on the provided criteria.
     * Combines all non-null criteria using AND logic.
     *
     * @param criteria The search filters.
     * @return A list of matching Funcionarios.
     */
    public List<Funcionario> searchFuncionarios(FuncionarioSearchCriteria criteria) {
        Stream<Funcionario> stream = getAllFuncionarios().stream();

        if (hasText(criteria.nomePartial())) {
            String query = criteria.nomePartial().toLowerCase();
            stream = stream.filter(f -> f.getNome() != null && f.getNome().toLowerCase().contains(query));
        }

        if (hasText(criteria.cpfExact())) {
            stream = stream.filter(f -> criteria.cpfExact().equals(f.getCpfCnpj()));
        }

        if (hasText(criteria.cargoExact())) {
            stream = stream.filter(f -> criteria.cargoExact().equalsIgnoreCase(f.getCargo()));
        }

        if (hasText(criteria.statusExact())) {
            stream = stream.filter(f -> criteria.statusExact().equalsIgnoreCase(f.getStatus()));
        }

        if (hasText(criteria.departamentoExact())) {
            stream = stream.filter(f -> criteria.departamentoExact().equalsIgnoreCase(f.getDepartamento()));
        }

        return stream.collect(Collectors.toList());
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

}