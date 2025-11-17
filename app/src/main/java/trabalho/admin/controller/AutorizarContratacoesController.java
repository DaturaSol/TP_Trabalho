package trabalho.admin.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import trabalho.admin.model.Usuario;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.model.Contratacao;
import trabalho.recrutamento.model.Contratacao.StatusContratacao;
import trabalho.recrutamento.model.RegimeContratacao;
import trabalho.recrutamento.model.Vaga;

public class AutorizarContratacoesController {

    // <editor-fold desc="FXML UI Components">
    @FXML
    private TextField idField;
    @FXML
    private ComboBox<RegimeContratacao> regimeComboBox;
    @FXML
    private ComboBox<StatusContratacao> statusComboBox;
    @FXML
    private TextField candidatoField;
    @FXML
    private TextField RecrutadorField; // Note: Capital 'R' matches FXML
    @FXML
    private DatePicker dataField;
    @FXML
    private Button autorizarButton;
    @FXML
    private TableView<Contratacao> contratacoesTableView;
    @FXML
    private TableColumn<Contratacao, String> idColumn;
    @FXML
    private TableColumn<Contratacao, String> regimeColumn;
    @FXML
    private TableColumn<Contratacao, String> statusColumn;
    @FXML
    private TableColumn<Contratacao, String> candidatoColumn;
    @FXML
    private TableColumn<Contratacao, String> recrutadorColumn;
    @FXML
    private TableColumn<Contratacao, String> dataColumn; // Solicitação
    @FXML
    private TableColumn<Contratacao, String> dataColumn1; // Autorização
    // </editor-fold>

    private Usuario currentUser;
    private ObservableList<Contratacao> masterContratacoesList = FXCollections.observableArrayList();

    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
        loadContratacoes();
    }

    @FXML
    private void initialize() {
        System.out.println("AutorizarContratacoesController initialized.");
        setupComboBoxes();
        setupTableColumns();
    }

    private void setupComboBoxes() {
        regimeComboBox.getItems().setAll(RegimeContratacao.values());
        statusComboBox.getItems().setAll(StatusContratacao.values());
    }

    private void setupTableColumns() {

        idColumn.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getId();
            return new SimpleStringProperty(id != null ? id : "");
        });

        regimeColumn.setCellValueFactory(cellData -> {
            RegimeContratacao regime = cellData.getValue().getRegime();
            return new SimpleStringProperty(regime != null ? regime.toString() : "");
        });

        statusColumn.setCellValueFactory(cellData -> {
            StatusContratacao status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.toString() : "");
        });

        dataColumn.setCellValueFactory(cellData -> {
            Date data = cellData.getValue().getDataSolicitacao();
            return new SimpleStringProperty(data != null ? data.toString() : "");
        });
        dataColumn1.setCellValueFactory(cellData -> {
            Date data = cellData.getValue().getDataAutorizacao();
            return new SimpleStringProperty(data != null ? data.toString() : "");
        });

        candidatoColumn.setCellValueFactory(cellData -> {
            String cpf = cellData.getValue().getCandidatoCpf();
            return new SimpleStringProperty(cpf != null ? cpf : "");
        });

        recrutadorColumn.setCellValueFactory(cellData -> {
            String cpf = cellData.getValue().getRecrutadorSolicitanteCpf();
            return new SimpleStringProperty(cpf != null ? cpf : "");
        });
    }

    private void loadContratacoes() {
        masterContratacoesList.setAll(JsonDataManager.getInstance().getData().getContratacoes());
        contratacoesTableView.setItems(masterContratacoesList);
        statusComboBox.setValue(StatusContratacao.PENDENTE_AUTORIZACAO);
        handleFiltroAction(null);
    }

    @FXML
    private void handleFiltroAction(ActionEvent event) {
        ObservableList<Contratacao> filteredList = masterContratacoesList.stream()
                .filter(this::matchesFilters)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        contratacoesTableView.setItems(filteredList);
    }

    private boolean matchesFilters(Contratacao contratacao) {
        String idFilter = idField.getText().trim();
        RegimeContratacao regimeFilter = regimeComboBox.getValue();
        StatusContratacao statusFilter = statusComboBox.getValue();
        String candidatoFilter = candidatoField.getText().trim();
        String recrutadorFilter = RecrutadorField.getText().trim();
        LocalDate dataFilter = dataField.getValue();

        if (!idFilter.isEmpty() && !String.valueOf(contratacao.getVagaId()).contains(idFilter))
            return false;
        if (regimeFilter != null && contratacao.getRegime() != regimeFilter)
            return false;
        if (statusFilter != null && contratacao.getStatus() != statusFilter)
            return false;
        if (!candidatoFilter.isEmpty() && !contratacao.getCandidatoCpf().contains(candidatoFilter))
            return false;
        if (!recrutadorFilter.isEmpty() && !contratacao.getRecrutadorSolicitanteCpf().contains(recrutadorFilter))
            return false;
        if (dataFilter != null && !contratacao.getDataSolicitacao().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(dataFilter))
            return false;

        return true;
    }

    @FXML
    private void handleAutorizarButtonAction(ActionEvent event) {
        Contratacao selectedContratacao = contratacoesTableView.getSelectionModel().getSelectedItem();
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        if (selectedContratacao == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione uma contratação para autorizar.");
            return;
        }

        if (selectedContratacao.getStatus() != StatusContratacao.PENDENTE_AUTORIZACAO) {
            showAlert(Alert.AlertType.INFORMATION, "Status Inválido",
                    "Esta contratação não está com status 'PENDENTE' e não pode ser autorizada.");
            return;
        }

        selectedContratacao.setStatus(StatusContratacao.AUTORIZADA);
        selectedContratacao.setDataAutorizacao(new Date()); // Set authorization date to now
        selectedContratacao.setGestorAutorizanteCpf(currentUser.getCpfCnpj());

        // You might also need to update the Vaga's status
        Vaga vaga = appData.getVagasById().get(selectedContratacao.getVagaId());
        if (vaga != null) {
            vaga.setStatus(Vaga.StatusVaga.FECHADA);
        }

        dataManager.saveData();

        // Refresh the table to show the updated status
        contratacoesTableView.refresh();

        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Contratação autorizada com sucesso!");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}