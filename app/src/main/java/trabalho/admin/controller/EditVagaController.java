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
import trabalho.financeiro.utils.CpfCnpjManager;
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
    private TextField cpfRecrutadorField;
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
    @FXML
    private ComboBox<StatusVaga> statusComboBox;
    @FXML
    private Button salvarButton;
    // </editor-fold>

    private Vaga vagaToEdit;
    private Usuario currentUser;
    private static final String STYLE_ERROR = "-fx-border-color: red; -fx-background-color: #ffdddd;";

    // For new Vaga creation
    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
        title.setText("Criar Nova Vaga");
    }

    // For editing existing Vaga
    public void initData(Usuario currentUser, Vaga vaga) {
        this.currentUser = currentUser;
        this.vagaToEdit = vaga;
        title.setText("Editar Vaga");
        populateFields(vaga);
    }

    @FXML
    private void initialize() {
        System.out.println("EditVagaController initialized.");
        // Populate the combo boxes with values from the enums
        regimeComboBox.getItems().setAll(RegimeContratacao.values());
        statusComboBox.getItems().setAll(StatusVaga.values());
    }

    private void populateFields(Vaga vaga) {
        cpfRecrutadorField.setText(vaga.getRecrutadorResponsavelCpf());
        cargoField.setText(vaga.getCargo());
        departamentoField.setText(vaga.getDepartamento());
        salarioField.setText(String.valueOf(vaga.getSalarioBase()));
        requisitosField.setText(vaga.getRequisitos());
        if (vaga.getRegimeContratacao() != null) {
            regimeComboBox.setValue(RegimeContratacao.valueOf(vaga.getRegimeContratacao().name()));
        }
        //regimeComboBox.setValue(vaga.getRegimeContratacao());
        statusComboBox.setValue(vaga.getStatus());

        if (vaga.getDataAbertura() != null) {
            LocalDate localDate = vaga.getDataAbertura().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            dataPicker.setValue(localDate);
        }
    }

    @FXML
    private void handleSalvarButtonAction(ActionEvent event) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        feedbackLabel.setText("");
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
            showError("Salário inválido. Insira um número válido.", salarioField);
            return;
        }

        String requisitos = requisitosField.getText();
        if (requisitos.isBlank()) {
            showError("O campo 'Requisitos' é obrigatório.", requisitosField);
            return;
        }

        RegimeContratacao regime = regimeComboBox.getValue();
        if (regime == null) {
            showError("Selecione um 'Regime' de contratação.", regimeComboBox);
            return;
        }

        StatusVaga status = statusComboBox.getValue();
        if (status == null) {
            showError("Selecione um 'Status' para a vaga.", statusComboBox);
            return;
        }

        String recrutadorCpf = cpfRecrutadorField.getText();

        if (recrutadorCpf.isBlank() || !CpfCnpjManager.isValid(recrutadorCpf)) {
            showError("Cpf/Cnpj Invalido!", cpfRecrutadorField);
            return;
        }

        recrutadorCpf = CpfCnpjManager.toOnlyNumbers(recrutadorCpf);

        if (!appData.getRecrutadores().containsKey(recrutadorCpf)) {
            showError("CPF do Recrutador não encontrado.", cpfRecrutadorField);
            return;
        }

        LocalDate localDate = dataPicker.getValue();
        if (localDate == null) {
            showError("Selecione uma 'Data de Abertura'.", dataPicker);
            return;
        }
        // Convert LocalDate back to java.util.Date for the Vaga object
        Date dataAbertura = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Vaga newOrUpdatedVaga;

        if (vagaToEdit != null) {
            vagaToEdit.setCargo(cargo);
            vagaToEdit.setDepartamento(departamento);
            vagaToEdit.setSalarioBase(salario);
            vagaToEdit.setRequisitos(requisitos);
            vagaToEdit.setRegimeContratacao(regime);
            vagaToEdit.setStatus(status);
            vagaToEdit.setRecrutadorResponsavelCpf(recrutadorCpf);
            vagaToEdit.setDataAbertura(dataAbertura);
            newOrUpdatedVaga = vagaToEdit;
        } else {
            newOrUpdatedVaga = new Vaga(
                    cargo,
                    departamento,
                    salario,
                    requisitos,
                    regime,
                    currentUser.getCpfCnpj());
            newOrUpdatedVaga.setStatus(status);
            newOrUpdatedVaga.setDataAbertura(dataAbertura);
            newOrUpdatedVaga.setRecrutadorResponsavelCpf(recrutadorCpf);
        }

        try {
            appData.addVaga(newOrUpdatedVaga);
            showSuccess("Vaga salva com sucesso!");
        } catch (DuplicateDataException e) {
            if (vagaToEdit == null) {
                showError("Erro ao salvar a vaga: " + e.getMessage(), null);
            } else {
                appData.removeVaga(newOrUpdatedVaga);
                try {
                    appData.addVaga(newOrUpdatedVaga);
                } catch (DuplicateDataException e1) {
                    // Should never happen
                    e1.printStackTrace();
                }
            }
        } finally {
            dataManager.saveData();
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        // Implement your back button logic here, likely returning to the Vagas panel
        System.out.println("Back button to be implemented.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/dashboardGestor.fxml"));
            Parent root = loader.load();

            DashboardGestorController controller = loader.getController();
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

    private void showError(String message, Control fieldToHighlight) {
        feedbackLabel.setTextFill(Color.RED);
        feedbackLabel.setText(message);
        if (fieldToHighlight != null) {
            fieldToHighlight.setStyle(STYLE_ERROR);
        }
    }

    private void showSuccess(String message) {
        feedbackLabel.setTextFill(Color.GREEN);
        feedbackLabel.setText(message);
    }
}