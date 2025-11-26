package trabalho.candidatura.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Pessoa;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.financeiro.utils.CpfCnpjManager;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

public class CadastroCandidatoController {

    // Dados Pessoais
    @FXML
    private TextField txtNomeCompleto;

    @FXML
    private TextField txtCpf;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextArea txtFormacao;

    @FXML
    private TextArea txtExperiencia;

    @FXML
    private Button btnSalvarDadosPessoais;

    // Dados Candidatura
    @FXML
    private TextField txtPretensaoSalarial;

    @FXML
    private ComboBox<String> comboDisponibilidade;

    @FXML
    private TextArea txtDocumentos;

    @FXML
    private DatePicker dateCadastro;

    @FXML
    private Button btnAdicionarDocumento;

    @FXML
    private Button btnSalvarCandidatura;

    @FXML
    private Button btnVoltar;

    //Metodo de inicialização
    @FXML
    public void initialize() {
        comboDisponibilidade.getItems().addAll(
                "Integral",
                "Manhã",
                "Tarde",
                "Noite",
                "Horário Flexível");

        dateCadastro.setValue(java.time.LocalDate.now());
    }

    @FXML
    private void salvarDadosPessoais(ActionEvent event) {
        String nome = txtNomeCompleto.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        String formacao = txtFormacao.getText();
        String experiencia = txtExperiencia.getText();

        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Campos obrigatórios", "Preencha Nome, CPF e Email.");
            return;
        } else if (!CpfCnpjManager.isValid(cpf)) {
            mostrarAlerta("Erro", "CPF inválido!");
            return;
        } else if (appData.getPessoas().containsKey(cpf)) {
            mostrarAlerta("Erro", "CPF já cadastrado!");
            return;
        } else {
            mostrarAlerta("Sucesso", "Dados pessoais salvos com sucesso!");
        }
    }

    @FXML
    private void salvarCandidatura(ActionEvent event) {
        try {
            String nome = txtNomeCompleto.getText();
            String cpf = txtCpf.getText();
            String email = txtEmail.getText();
            String formacao = txtFormacao.getText();
            String experiencia = txtExperiencia.getText();
            String disponibilidade = comboDisponibilidade.getValue();
            String documentos = txtDocumentos.getText();
            java.time.LocalDate data = dateCadastro.getValue();

            // Remover "R$" e converter para double
            String salarioStr = txtPretensaoSalarial.getText().replace("R$", "").trim();
            double pretensaoSalarial = Double.parseDouble(salarioStr);
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();

            // Verificação se Pessoa já existe
            if (!appData.getPessoas().containsKey(cpf)) {
                Pessoa pessoa = new Pessoa(cpf, nome, email);
                appData.addPessoa(pessoa);
                dataManager.saveData();
            }

            // Criar o objeto candidato
            Candidato candidato = new Candidato(
                    cpf,
                    formacao,
                    experiencia,
                    pretensaoSalarial,
                    disponibilidade,
                    documentos,
                    Date.valueOf(data).toLocalDate());

            boolean sucesso = Candidato.cadastrarCandidato(candidato);

            if (sucesso) {
                mostrarAlerta("Sucesso", "Candidato cadastrado com sucesso!");
                limparCampos();
            }

        } catch (Exception e) {
            mostrarAlerta("Erro", "Verifique os dados informados.\n");
        }
    }

    private void limparCampos() {
        txtNomeCompleto.clear();
        txtCpf.clear();
        txtEmail.clear();
        txtFormacao.clear();
        txtExperiencia.clear();
        txtPretensaoSalarial.setText("R$ ");
        comboDisponibilidade.getSelectionModel().clearSelection();
        txtDocumentos.clear();
        dateCadastro.setValue(java.time.LocalDate.now());
    }

    @FXML
    private void adicionarDocumento(ActionEvent event) {
        // Cria o seletor de arquivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Documento");

        // Abre a janela de seleção (usa a janela atual como referência)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);

        // Se o usuário escolheu um arquivo
        if (arquivoSelecionado != null) {
            String nomeArquivo = arquivoSelecionado.getName();
            // Adiciona o nome do arquivo ao TextArea
            txtDocumentos.appendText(nomeArquivo + "\n");
        }
    }

    private Usuario currentUser;
    
    @FXML
    private Button backButton;

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/candidatura/inicio.fxml"));
            Parent root = loader.load();

            InicioController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Candidatura");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initData(Usuario user) {
        this.currentUser = user;
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
