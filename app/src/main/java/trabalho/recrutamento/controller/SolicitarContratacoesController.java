package trabalho.recrutamento.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidatura;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.Contratacao;
import trabalho.recrutamento.model.Entrevista;
import trabalho.recrutamento.model.RegimeContratacao;
import trabalho.recrutamento.model.Vaga;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para recrutador solicitar contratação
 * Apenas SOLICITA - não autoriza
 */
public class SolicitarContratacoesController {

    @FXML private ComboBox<Entrevista> comboEntrevista;
    @FXML private ComboBox<String> comboRegime;
    @FXML private TextArea txtObservacoes;
    @FXML private TextArea txtDadosEntrevista;
    @FXML private Button btnSolicitar;
    @FXML private Button btnLimpar;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        // Popular regime
        comboRegime.getItems().addAll("CLT", "ESTAGIO", "PJ");
        
        // Carregar entrevistas aprovadas (nota >= 7)
        List<Entrevista> entrevistas = getEntrevistasAprovadas();
        
        comboEntrevista.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entrevista e, boolean empty) {
                super.updateItem(e, empty);
                if (empty || e == null) {
                    setText(null);
                } else {
                    Candidatura c = getCandidatura(e.getCandidatoCpf());
                    if (c != null) {
                        setText(c.getCandidato().getPessoa().getNome() + " — Nota: " + e.getNota());
                    }
                }
            }
        });
        
        comboEntrevista.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Entrevista e, boolean empty) {
                super.updateItem(e, empty);
                if (empty || e == null) {
                    setText(null);
                } else {
                    Candidatura c = getCandidatura(e.getCandidatoCpf());
                    if (c != null) {
                        setText(c.getCandidato().getPessoa().getNome() + " — Nota: " + e.getNota());
                    }
                }
            }
        });
        
        comboEntrevista.getItems().addAll(entrevistas);
        comboEntrevista.setOnAction(e -> mostrarDadosEntrevista());
        
        btnSolicitar.setOnAction(e -> solicitar());
        btnLimpar.setOnAction(e -> limpar());
    }

    private Usuario currentUser;

    private void solicitar() {
        try {
            Entrevista entrevista = comboEntrevista.getValue();
            String regime = comboRegime.getValue();
            
            if (entrevista == null || regime == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", 
                    "Selecione entrevista e regime de contratação.");
                return;
            }
            
            JsonDataManager dataManager = JsonDataManager.getInstance();
            AppData appData = dataManager.getData();
            
            Contratacao contratacao = new Contratacao();
            contratacao.setId("CTR" + System.currentTimeMillis());
            contratacao.setCandidatoCpf(entrevista.getCandidatoCpf());
            contratacao.setVagaId(entrevista.getVagaId());
            contratacao.setRegimeContratacao(RegimeContratacao.valueOf(regime));
            contratacao.setDataSolicitacao(LocalDate.now());
            contratacao.setStatus(Contratacao.StatusContratacao.PENDENTE_AUTORIZACAO);
            contratacao.setObservacoes(txtObservacoes.getText());
            
            appData.addContratacao(contratacao);
            dataManager.saveData();
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", 
                "Solicitação de contratação enviada! Aguardando autorização do gestor.");
            limpar();
            
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao solicitar: " + e.getMessage());
        }
    }

    private void mostrarDadosEntrevista() {
        Entrevista e = comboEntrevista.getValue();
        if (e != null) {
            Candidatura c = getCandidatura(e.getCandidatoCpf());
            if (c != null) {
                txtDadosEntrevista.setText(
                    "Candidato: " + c.getCandidato().getPessoa().getNome() + "\n" +
                    "CPF: " + c.getCandidato().getCpfCnpj() + "\n" +
                    "Vaga: " + (c.getVaga() != null ? c.getVaga().getCargo() : "N/A") + "\n" +
                    "Data Entrevista: " + e.getDataHora() + "\n" +
                    "Nota: " + e.getNota() + "\n" +
                    "Avaliador: " + e.getAvaliadorCpf() + "\n" 
                    // + "Observações: " + (e.getObservacoes() != null ? e.getObservacoes() : "Nenhuma")
                );
            }
        }
    }

    private void limpar() {
        comboEntrevista.setValue(null);
        comboRegime.setValue(null);
        txtObservacoes.clear();
        txtDadosEntrevista.clear();
    }

    private List<Entrevista> getEntrevistasAprovadas() {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        return appData.getEntrevistas().stream()
            .filter(e -> e.getNota() != null && e.getNota() >= 7.0)
            .collect(Collectors.toList());
    }

    private Candidatura getCandidatura(String cpf) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        return appData.getCandidaturas().stream()
            .filter(c -> c.getCpfCnpjCandidato().equals(cpf))
            .findFirst()
            .orElse(null);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/recrutamento/menu_recrutamento.fxml"));
            Parent root = loader.load();

            MenuRecrutamentoController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initData(Usuario user) {
        this.currentUser = user;
    }
}
