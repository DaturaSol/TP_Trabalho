package trabalho.common.database;

import trabalho.admin.model.Usuario;
import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.Recrutador;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.candidatura.model.Pessoa;
import trabalho.exceptions.DuplicateDataException;
import trabalho.exceptions.MissingDataException;
import trabalho.recrutamento.model.Vaga;
import trabalho.recrutamento.model.Entrevista;
import trabalho.recrutamento.model.Contratacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A container class that acts as an in-memory database for the application.
 * Each type of object is stored in its own separate list for simplicity and
 * clarity.
 * This is really dumb, but due to time constrains, we are just gonna store it
 * all
 * and load it.
 * 
 * @author Gabriel M.S.O.
 */
public class AppData {
    // For fast lookups lets organize them by cpf
    private Map<String, Pessoa> pessoasByCpf;
    private Map<String, Usuario> usuariosByCpf;

    private Map<String, Funcionario> funcionariosByCpf;

    private Map<String, Administrador> administradoresByCpf;
    private Map<String, Gestor> gestoresByCpf;
    private Map<String, Recrutador> recrutadoresByCpf;

    private Map<String, Candidato> candidatosByCpf;

    private Map<String, Vaga> vagasById;
    private List<Candidatura> candidaturas;
    private List<Entrevista> entrevistas;
    public List<Contratacao> contratacoes;

    public AppData() {
        this.pessoasByCpf = new HashMap<>();
        this.usuariosByCpf = new HashMap<>();

        this.funcionariosByCpf = new HashMap<>();

        this.administradoresByCpf = new HashMap<>();
        this.gestoresByCpf = new HashMap<>();
        this.recrutadoresByCpf = new HashMap<>();

        this.candidatosByCpf = new HashMap<>();

        this.vagasById = new HashMap<>();
        this.candidaturas = new ArrayList<>();
        this.entrevistas = new ArrayList<>();
        this.contratacoes = new ArrayList<>();

    }

    // --- Start Getters ---
    public Map<String, Pessoa> getPessoas() {
        return pessoasByCpf;
    }

    public Map<String, Usuario> getUsuarios() {
        return usuariosByCpf;
    }

    public Map<String, Administrador> getAdministradores() {
        return administradoresByCpf;
    }

    public Map<String, Gestor> getGestores() {
        return gestoresByCpf;
    }

    public Map<String, Recrutador> getRecrutadores() {
        return recrutadoresByCpf;
    }

    public Map<String, Funcionario> getFuncionarios() {
        return funcionariosByCpf;
    }

    public Map<String, Candidato> getCandidatos() {
        return candidatosByCpf;
    }

    public Map<String, Vaga> getVagasById() {
        return vagasById;
    }

    public List<Candidatura> getCandidaturas() {
        return candidaturas;
    }

    public List<Entrevista> getEntrevistas() {
        return entrevistas;
    }

    public List<Contratacao> getContratacoes() {
        return contratacoes;
    }

    // --- End Getters ---

    // --- Start Setters ---
    public void addPessoa(Pessoa pessoa) throws DuplicateDataException {
        String cpf = pessoa.getCpfCnpj();
        if (pessoasByCpf.containsKey(cpf)) {
            throw new DuplicateDataException("Pessoa com CPF/CNPJ " + cpf + " já existe.");
        }
        pessoasByCpf.put(cpf, pessoa);
    }

    public void addUsuario(Usuario usuario) throws DuplicateDataException, MissingDataException {
        String cpf = usuario.getCpfCnpj();
        if (usuariosByCpf.containsKey(cpf)) {
            throw new DuplicateDataException("Usuário com CPF/CNPJ " + cpf + " já existe.");
        }
        if (!pessoasByCpf.containsKey(cpf)) {
            throw new MissingDataException("Pessoa com CPF/CNPJ" + cpf + "Não existe");
        }
        usuariosByCpf.put(cpf, usuario);
    }

    public void addFuncionario(Funcionario f) throws DuplicateDataException, MissingDataException {
        String cpf = f.getCpfCnpj();
        if (!pessoasByCpf.containsKey(cpf)) {
            throw new MissingDataException("Pessoa com CPF/CNPJ" + cpf + "Não existe");
        }
        switch (f) {
            case Administrador admin -> {
                if (administradoresByCpf.containsKey(cpf)) {
                    throw new DuplicateDataException("Administrador com CPF/CNPJ " + cpf + " já existe.");
                }
                administradoresByCpf.put(cpf, admin);
            }

            case Gestor gestor -> {
                if (gestoresByCpf.containsKey(cpf)) {
                    throw new DuplicateDataException("Gestor com CPF/CNPJ " + cpf + " já existe.");
                }
                gestoresByCpf.put(cpf, gestor);
            }

            case Recrutador recrutador -> {
                if (recrutadoresByCpf.containsKey(cpf)) {
                    throw new DuplicateDataException("Recrutador com CPF/CNPJ " + cpf + " já existe.");
                }
                recrutadoresByCpf.put(cpf, recrutador);
            }

            default -> {
                if (funcionariosByCpf.containsKey(cpf)) {
                    throw new DuplicateDataException("Funcionário com CPF/CNPJ " + cpf + " já existe.");
                }
                funcionariosByCpf.put(cpf, f);
            }
        }

    }

    public void addCandidato(Candidato c) throws DuplicateDataException {
        String cpf = c.getCpfCnpj();
        if (candidatosByCpf.containsKey(cpf)) {
            throw new DuplicateDataException("Candidato com CPF/CNPJ " + cpf + " já existe.");
        }
        candidatosByCpf.put(cpf, c);
    }

    public void addVaga(Vaga v) throws DuplicateDataException {
        String id = v.getId();
        if (vagasById.containsKey(id)) {
            throw new DuplicateDataException("Vaga já existe.");
        }
        vagasById.put(id, v);
    }

    public void addCandidatura(Candidatura c) throws DuplicateDataException {
        if (candidaturas.contains(c)) {
            throw new DuplicateDataException("Candidatura já existe.");
        }
        candidaturas.add(c);
    }

    public void addEntrevista(Entrevista e) throws DuplicateDataException {
        if (entrevistas.contains(e)) {
            throw new DuplicateDataException("Entrevista já existe.");
        }
        entrevistas.add(e);
    }

    public void addContratacao(Contratacao con) throws DuplicateDataException {
        if (contratacoes.contains(con)) {
            throw new DuplicateDataException("Contratação já existe.");
        }
        contratacoes.add(con);
    }
    // --- End Setters ---

    // --- Start Removers ---
    public void removePessoa(Pessoa p) {
        pessoasByCpf.remove(p.getCpfCnpj());
    }

    public void removeUsuario(Usuario u) {
        usuariosByCpf.remove(u.getCpfCnpj());
    }

    public void removeFuncionario(Funcionario f) {
        String cpf = f.getCpfCnpj();
        switch (f) {
            case Administrador admin -> administradoresByCpf.remove(cpf);
            case Gestor gestor -> gestoresByCpf.remove(cpf);
            case Recrutador recrutador -> recrutadoresByCpf.remove(cpf);
            default -> funcionariosByCpf.remove(cpf);
        }
    }

    public void removeCandidato(Candidato c) {
        candidatosByCpf.remove(c.getCpfCnpj());
    }

    public void removeVaga(Vaga v) {
        vagasById.remove(v.getId());
    }

    public void removeCandidatura(Candidatura c) {
        candidaturas.remove(c);
    }
    // --- End Removers ---

    // --- Utils ---

    /**
     * Retrieves a list of all Funcionarios in the system.
     */
    public List<Funcionario> getAllFuncionarios() {
        List<Funcionario> allUsers = new ArrayList<>();

        allUsers.addAll(administradoresByCpf.values());
        allUsers.addAll(gestoresByCpf.values());
        allUsers.addAll(recrutadoresByCpf.values());
        allUsers.addAll(funcionariosByCpf.values());
        // Using a Set to remove duplicates if a person is in multiple lists (e.g.,
        // gestor and funcionario)
        return new ArrayList<>(new LinkedHashSet<>(allUsers));
    }

}