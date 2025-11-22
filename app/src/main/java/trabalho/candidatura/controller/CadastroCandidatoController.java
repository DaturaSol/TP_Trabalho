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
import trabalho.candidatura.model.Candidato;
import trabalho.candidatura.model.Pessoa;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.financeiro.utils.CpfCnpjManager;

import java.io.File;
import java.io.IOException;

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

    // Botão Voltar
    @FXML
    private Button btnVoltar;

    /**
     * Metodo de inicialização
     */
    @FXML
    public void initialize() {

        comboDisponibilidade.getItems().addAll(
                "Integral",
                "Manhã",
                "Tarde",
                "Noite",
                "Horário Flexível");

        // Define a data atual como padrão no DatePicker
        dateCadastro.setValue(java.time.LocalDate.now());
    }

    /**
     * Ação do botão "Salvar" na aba de Dados Pessoais
     */
    @FXML
    private void salvarDadosPessoais(ActionEvent event) {
        String nome = txtNomeCompleto.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        String formacao = txtFormacao.getText();
        String experiencia = txtExperiencia.getText();

        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        // Validação simples
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

    /**
     * Ação do botão "Salvar" na aba de Dados da Candidatura
     */
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

            // Gotta verify if Pessoa already exists, if doesn't, create it
            if (!appData.getPessoas().containsKey(cpf)) {
                Pessoa pessoa = new Pessoa(cpf, nome, email);
                appData.addPessoa(pessoa);
                dataManager.saveData(); // gotta save 
            }

            // Criar o objeto candidato
            Candidato candidato = new Candidato(
                    cpf,
                    formacao,
                    experiencia,
                    pretensaoSalarial,
                    disponibilidade,
                    documentos,
                    java.sql.Date.valueOf(data));

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

    /**
     * Ação do botão "Adicionar Documento"
     */
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

    /**
     * Ação do botão "Voltar"
     */
    @FXML
    private void voltarTela(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/candidatura/inicio.fxml"));
        Parent root = loader.load();

        // Pega a janela atual (Stage)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Substitui o conteúdo pela nova tela
        stage.setScene(new Scene(root));
        stage.setTitle("Menu Principal");
        stage.show();
    }

    /**
     * Metodo utilitário para exibir alertas
     */
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
