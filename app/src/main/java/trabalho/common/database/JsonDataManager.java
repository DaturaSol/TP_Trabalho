package trabalho.common.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Candidatura;
import trabalho.candidatura.model.Pessoa;
import trabalho.exceptions.DuplicateDataException;
import trabalho.exceptions.MissingDataException;
import trabalho.financeiro.model.Funcionario;
import trabalho.financeiro.utils.CpfCnpjManager;
import trabalho.financeiro.utils.PasswordManager;
import trabalho.recrutamento.model.Contratacao;
import trabalho.recrutamento.model.Recrutador;
import trabalho.recrutamento.model.RegimeContratacao;
import trabalho.recrutamento.model.Vaga;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Manages data persistence using a single JSON file.
 * Configured to handle the polymorphic Pessoa hierarchy.
 * 
 * @author Gabriel M.S.O.
 */
public class JsonDataManager {
    private static final String DEFAULT_JSON_FILE = "hr_data.json";
    private static JsonDataManager instance;

    private final String jsonFile;
    private AppData data;
    private final Gson gson;

    private JsonDataManager(String fileName) {
        this.jsonFile = fileName;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        loadData();
    }

    public static synchronized JsonDataManager getInstance() {
        return getInstance(DEFAULT_JSON_FILE);
    }

    public static synchronized JsonDataManager getInstance(String filename) {
        if (instance == null) {
            instance = new JsonDataManager(filename);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public AppData getData() {
        return data;
    }

    /**
     * Loads application data from the JSON file with a robust, clean flow.
     * It handles file not found, empty/invalid files, and parsing errors
     * gracefully.
     */
    private void loadData() {
        if (Files.exists(Paths.get(this.jsonFile))) {
            try (Reader reader = new FileReader(this.jsonFile)) {
                AppData loadedData = gson.fromJson(reader, AppData.class);

                if (loadedData != null && loadedData.getAdministradores() != null) { // At least one ADM must exist
                    // Success!
                    this.data = loadedData;
                    System.out.println("Data loaded successfully from " + this.jsonFile);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error reading/parsing JSON file. Starting fresh. Error: " + e.getMessage());
            }
        }

        System.out.println("Initializing a new, empty data set in memory.");
        initializeEmptyData();
    }

    public void initializeDummyData() throws DuplicateDataException, MissingDataException {
        String adminCpf = CpfCnpjManager.toOnlyNumbers("000.000.000-00");
        Pessoa adminPessoa = new Pessoa(adminCpf, "Admin do Sistema");
        Usuario adminUsuario = new Usuario(adminCpf,
                PasswordManager.hashPassword("admin"));
        Administrador adminFuncionario = new Administrador(adminCpf, "Administrador Sistema",
                "Ativo", "TI", 10000.0);
        Gestor adminAsGestor = new Gestor(adminCpf, "Admin (as Gestor)",
                "Ativo", "TI", 10000.0);
        Recrutador adminAsRecrutador = new Recrutador(adminCpf, "Admin (as Recrutador)",
                "Ativo", "TI", 10000.0);
        Funcionario adminAsFuncionario = new Funcionario(adminCpf, "Admin (as Funcionario)",
                "Ativo", "TI", 10000.0);

        Candidato candidatoTeste = new Candidato(adminCpf);

        Vaga vagaTeste = new Vaga("cargo", "departamento",
                1200.00, "Muita coisa\n muitacoisa", RegimeContratacao.CLT,
                adminCpf);
        Candidatura candidaturaTest = new Candidatura(
                adminCpf,
                vagaTeste,
                new Date());
        Contratacao contratacaoTest = new Contratacao();

        this.data.addPessoa(adminPessoa);
        this.data.addUsuario(adminUsuario);
        this.data.addFuncionario(adminFuncionario);
        this.data.addFuncionario(adminAsGestor);
        this.data.addFuncionario(adminAsRecrutador);
        this.data.addFuncionario(adminAsFuncionario);
        this.data.addVaga(vagaTeste);
        this.data.addCandidato(candidatoTeste);
        this.data.addCandidatura(candidaturaTest);
        this.data.addContratacao(contratacaoTest);
    }

    public void saveData() {
        try (Writer writer = new FileWriter(this.jsonFile)) {
            gson.toJson(this.data, writer);
            System.out.println("Data saved to " + this.jsonFile);
        } catch (IOException e) {
            System.err.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    private void initializeEmptyData() {
        this.data = new AppData();
        try {
            initializeDummyData();
            saveData();
        } catch (Exception e) {
            System.err.println("Error initializing dummy data: " + e.getMessage());
        }
    }
}