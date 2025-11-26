package trabalho.admin.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import trabalho.admin.model.Usuario;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.exceptions.DuplicateDataException;
import trabalho.recrutamento.model.Recrutador;
import trabalho.recrutamento.model.RegimeContratacao;
import trabalho.recrutamento.model.Vaga;
import trabalho.recrutamento.model.Vaga.StatusVaga;

public class EditVagaController {

    // <editor-fold desc="FXML UI Components">
    @FXML
    private Button backButton;
    @FXML
    private Label title;
    @FXML
    private Label feedbackLabel;
    @FXML
    private ComboBox<Recrutador> recrutadorComboBox;
    @FXML
    private TextField cargoField;
    @FXML
    private TextField departamentoField;
    @FXML
    private TextField salarioField;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private TextArea requisitosField;
    @FXML
    private ComboBox<RegimeContratacao> regimeComboBox;

    // CHANGED: Use String to match the working controller pattern
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button salvarButton;
    // </editor-fold>

    private Vaga vagaToEdit;
    private Usuario currentUser;
    private static final String STYLE_ERROR = "-fx-border-color: red; -fx-background-color: #ffdddd;";

    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
        title.setText("Criar Nova Vaga");
        // Set default text
        statusComboBox.setValue(StatusVaga.ABERTA.getDescricao());
    }

    public void initData(Usuario currentUser, Vaga vaga) {
        this.currentUser = currentUser;
        this.vagaToEdit = vaga;
        title.setText("Editar Vaga");
        populateFields(vaga);
    }

    @FXML
    private void initialize() {
        AppData appData = JsonDataManager.getInstance().getData();

        recrutadorComboBox.getItems().setAll(appData.getRecrutadores().values());
        regimeComboBox.getItems().setAll(RegimeContratacao.values());

        for (StatusVaga s : StatusVaga.values()) {
            statusComboBox.getItems().add(s.getDescricao());
        }
    }

    private void populateFields(Vaga vaga) {
        AppData appData = JsonDataManager.getInstance().getData();

        if (vaga.getRecrutadorResponsavelCpf() != null) {
            recrutadorComboBox.setValue(appData.getRecrutadores().get(vaga.getRecrutadorResponsavelCpf()));
        }

        cargoField.setText(vaga.getCargo());
        departamentoField.setText(vaga.getDepartamento());
        salarioField.setText(String.valueOf(vaga.getSalarioBase()));
        requisitosField.setText(vaga.getRequisitos());

        if (vaga.getRegimeContratacao() != null) {
            regimeComboBox.setValue(vaga.getRegimeContratacao());
        }

        if (vaga.getStatus() != null) {
            statusComboBox.setValue(vaga.getStatus().getDescricao());
        } else {
            statusComboBox.setValue(StatusVaga.ABERTA.getDescricao());
        }

        if (vaga.getDataAbertura() != null) {
            LocalDate localDate = vaga.getDataAbertura().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            dataPicker.setValue(localDate);
        }
    }

    @FXML
    private void handleSalvarButtonAction(ActionEvent event) {
        resetFieldStyles();
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        // [Field Validations kept same as before...]
        String cargo = cargoField.getText();
        if (cargo.isBlank()) {
            showError("O campo 'Cargo' é obrigatório.", cargoField);
            return;
        }
        String departamento = departamentoField.getText();
        if (departamento.isBlank()) {
            showError("O campo 'Departamento' é obrigatório.", departamentoField);
            return;
        }
        double salario;
        try {
            salario = Double.parseDouble(salarioField.getText());
        } catch (NumberFormatException e) {
            showError("Salário inválido.", salarioField);
            return;
        }
        String requisitos = requisitosField.getText();
        if (requisitos.isBlank()) {
            showError("O campo 'Requisitos' é obrigatório.", requisitosField);
            return;
        }
        RegimeContratacao regime = regimeComboBox.getValue();
        if (regime == null) {
            showError("Selecione um 'Regime'.", regimeComboBox);
            return;
        }

        Recrutador recrutador = recrutadorComboBox.getValue();
        if (recrutador == null) {
            showError("Selecione um Recrutador.", recrutadorComboBox);
            return;
        }
        LocalDate localDate = dataPicker.getValue();
        if (localDate == null) {
            showError("Selecione uma data.", dataPicker);
            return;
        }

        // CHANGED: Get String from UI -> Convert to Enum Object
        String statusDescricao = statusComboBox.getValue();
        StatusVaga statusSelecionado = null;

        if (statusDescricao == null) {
            showError("Selecione um 'Status'.", statusComboBox);
            return;
        }

        // Find the matching Enum based on the description
        for (StatusVaga s : StatusVaga.values()) {
            if (s.getDescricao().equals(statusDescricao)) {
                statusSelecionado = s;
                break;
            }
        }

        if (statusSelecionado == null) {
            // Fallback
            statusSelecionado = StatusVaga.ABERTA;
        }

        Date dataAbertura = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            if (vagaToEdit != null) {
                // Update
                vagaToEdit.setCargo(cargo);
                vagaToEdit.setDepartamento(departamento);
                vagaToEdit.setSalarioBase(salario);
                vagaToEdit.setRequisitos(requisitos);
                vagaToEdit.setRegimeContratacao(regime);
                vagaToEdit.setStatus(statusSelecionado); // Set the Enum Object found above
                vagaToEdit.setRecrutadorResponsavelCpf(recrutador.getCpfCnpj());
                vagaToEdit.setDataAbertura(dataAbertura);

                showSuccess("Vaga atualizada com sucesso!");
            } else {
                // Create
                Vaga newVaga = new Vaga(cargo, departamento, salario, requisitos, regime, currentUser.getCpfCnpj());
                newVaga.setStatus(statusSelecionado);
                newVaga.setDataAbertura(dataAbertura);
                newVaga.setRecrutadorResponsavelCpf(recrutador.getCpfCnpj());

                appData.addVaga(newVaga);
                showSuccess("Vaga criada com sucesso!");
            }

            dataManager.saveData();

        } catch (DuplicateDataException e) {
            showError("Erro: " + e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao salvar: " + e.getMessage(), null);
        }
    }

    // Helper methods kept same as original
    private void resetFieldStyles() {
        feedbackLabel.setText("");
        cargoField.setStyle("");
        departamentoField.setStyle("");
        salarioField.setStyle("");
        requisitosField.setStyle("");
        regimeComboBox.setStyle("");
        statusComboBox.setStyle("");
        recrutadorComboBox.setStyle("");
        dataPicker.setStyle("");
    }

    private void showError(String message, Control fieldToHighlight) {
        feedbackLabel.setTextFill(Color.RED);
        feedbackLabel.setText(message);
        if (fieldToHighlight != null)
            fieldToHighlight.setStyle(STYLE_ERROR);
    }

    private void showSuccess(String message) {
        feedbackLabel.setTextFill(Color.GREEN);
        feedbackLabel.setText(message);
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/dashboardGestor.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Gestor");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}