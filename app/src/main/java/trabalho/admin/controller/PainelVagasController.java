package trabalho.admin.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.admin.model.Usuario;
import trabalho.financeiro.model.Funcionario;
import trabalho.recrutamento.model.RegimeContratacao;
import trabalho.recrutamento.model.Vaga;
import trabalho.recrutamento.model.Vaga.StatusVaga;
import trabalho.recrutamento.model.Recrutador;

/**
 * Controller for the job vacancy management panel (AutorizarContratacoes.fxml).
 * This class handles displaying, searching, creating, and editing job
 * vacancies.
 */
public class PainelVagasController {

    // --- Top/Bottom Buttons ---
    @FXML
    private Button backButton;

    @FXML
    private Button novaVagaButton;

    @FXML
    private Button editarVagaButton;

    @FXML
    private Button excluirVagaButton;

    // --- Left Search/Filter Fields ---
    @FXML
    private TextField idField;

    @FXML
    private TextField cargoField;

    @FXML
    private TextField departamentoField;

    @FXML
    private TextField salarioField;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField cpfRecrutadorField;

    @FXML
    private ComboBox<RegimeContratacao> regimeComboBox;

    @FXML
    private ComboBox<StatusVaga> statusComboBox;

    @FXML
    private Button pesquisarButton;

    // --- Center TableView ---
    @FXML
    private TableView<Vaga> vagasTableView;

    @FXML
    private TableColumn<Vaga, String> idColumn;

    @FXML
    private TableColumn<Vaga, String> cargoColumn;

    @FXML
    private TableColumn<Vaga, String> departamentoColumn;

    @FXML
    private TableColumn<Vaga, Double> salarioColumn;

    @FXML
    private TableColumn<Vaga, String> dataColumn;

    @FXML
    private TableColumn<Vaga, String> cpfRecrutadorColumn;

    @FXML
    private TableColumn<Vaga, String> regimeColumn;

    @FXML
    private TableColumn<Vaga, String> statusColumn;

    private ObservableList<Vaga> vagasList = FXCollections.observableArrayList();
    private Usuario currentUser;

    public void initData(Usuario currentUser) {
        System.out.println("Painel vaga controller initialized by user: " + currentUser.getCpfCnpj());
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It sets up table columns, combo boxes,
     * and loads initial data.
     */
    @FXML
    private void initialize() {
        System.out.println("AutorizarContratacoesController initialized.");

        populateComboBoxes();
        configureTableColumns();
        loadVagas();
        setupTableSelectionListener();
    }

    /**
     * Populates the ComboBoxes with predefined values (e.g., from Enums).
     */
    private void populateComboBoxes() {
        regimeComboBox.setItems(FXCollections.observableArrayList(RegimeContratacao.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(StatusVaga.values()));
    }

    /**
     * Configures the cell value factories for each table column to display data
     * from the Vaga objects.
     */
    private void configureTableColumns() {

        idColumn.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getId();
            return new SimpleStringProperty(id != null ? id : "");
        });

        cargoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        departamentoColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        salarioColumn.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getSalarioBase()).asObject());

        dataColumn.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getDataAbertura();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        cpfRecrutadorColumn.setCellValueFactory(cellData -> {
            Recrutador recrutador = cellData.getValue().getRecrutadorResponsavel();
            return new SimpleStringProperty(recrutador != null ? recrutador.toString() : "");
        });

        regimeColumn.setCellValueFactory(cellData -> {
            RegimeContratacao regime = cellData.getValue().getRegimeContratacao();
            return new SimpleStringProperty(regime != null ? regime.toString() : "");
        });

        statusColumn.setCellValueFactory(cellData -> {
            StatusVaga status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.toString() : "");
        });
    }

    /**
     * Fetches job vacancy data from the data source and populates the TableView.
     */
    private void loadVagas() {
        AppData appData = JsonDataManager.getInstance().getData();
        vagasList.setAll(appData.getVagasById().values().stream().toList());
        vagasTableView.setItems(vagasList);
        vagasTableView.refresh(); // Good practice to refresh the table view
    }

    /**
     * Sets up a listener on the table's selection model to enable or disable
     * the 'Editar Vaga' button based on whether a row is selected.
     */
    private void setupTableSelectionListener() {
        editarVagaButton.setDisable(true);
        excluirVagaButton.setDisable(true);
        vagasTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editarVagaButton.setDisable(newSelection == null);
            excluirVagaButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handlePesquisarButtonAction(ActionEvent event) {
        System.out.println("'Pesquisar' button clicked.");

        // Create a stream from the original full list of vacancies
        ObservableList<Vaga> filteredList = vagasList.stream()
                .filter(vaga -> matchesFilters(vaga))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        vagasTableView.setItems(filteredList);
    }

    /**
     * Helper method to check if a Vaga object matches the current filter criteria.
     * 
     * @param vaga The Vaga to check.
     * @return true if it matches, false otherwise.
     */
    private boolean matchesFilters(Vaga vaga) {
        AppData appData = JsonDataManager.getInstance().getData();
        String idFilter = idField.getText().trim();
        String cargoFilter = cargoField.getText().trim().toLowerCase();
        String departamentoFilter = departamentoField.getText().trim().toLowerCase();
        String salarioFilter = salarioField.getText().trim();
        LocalDate dataFilter = dataPicker.getValue();
        String cpfRecrutadorFilter = cpfRecrutadorField.getText().trim().toLowerCase();
        RegimeContratacao regimeFilter = regimeComboBox.getValue();
        StatusVaga statusFilter = statusComboBox.getValue();

        if (!idFilter.isEmpty() && !String.valueOf(vaga.getId()).contains(idFilter))
            return false;
        if (!cargoFilter.isEmpty() && !vaga.getCargo().toLowerCase().contains(cargoFilter))
            return false;
        if (!departamentoFilter.isEmpty() && !vaga.getDepartamento().toLowerCase().contains(departamentoFilter))
            return false;
        if (!salarioFilter.isEmpty() && !String.valueOf(vaga.getSalarioBase()).contains(salarioFilter))
            return false;
        if (dataFilter != null && !vaga.getDataAbertura().toInstant().atZone(java.time.ZoneId.systemDefault())
                .toLocalDate().equals(dataFilter))
            return false;
        if (!cpfRecrutadorFilter.isEmpty()) {
            Funcionario recrutador = appData.getRecrutadores().get(vaga.getRecrutadorResponsavelCpf());
            if (recrutador == null
                    || !recrutador.getPessoa(appData).getNome().toLowerCase().contains(cpfRecrutadorFilter)) {
                return false;
            }
        }
        if (regimeFilter != null && vaga.getRegimeContratacao() != regimeFilter)
            return false;
        if (statusFilter != null && vaga.getStatus() != statusFilter)
            return false;

        return true;
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/dashboardGestor.fxml"));
            Parent root = loader.load();

            DashboardGestorController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Gestor");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNovaVagaButtonAction(ActionEvent event) {
        System.out.println("'Nova Vaga' button clicked.");
        // Assuming there is an editVaga.fxml for creating/editing vacancies
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/editVaga.fxml"));
            Parent root = loader.load();

            EditVagaController controller = loader.getController();
            System.out.println("Nova vaga clicked by user: " + this.currentUser.getCpfCnpj());
            controller.initData(this.currentUser);

            Stage stage = (Stage) novaVagaButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Nova Vaga");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditarVagaButtonAction(ActionEvent event) {
        System.out.println("'Editar Vaga' button clicked.");
        Vaga selectedVaga = vagasTableView.getSelectionModel().getSelectedItem();

        if (selectedVaga != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/editVaga.fxml"));
                Parent root = loader.load();

                EditVagaController controller = loader.getController();
                controller.initData(this.currentUser, selectedVaga);

                Stage stage = (Stage) editarVagaButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Editar Vaga");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirVagaButtonAction(ActionEvent event) {
        Vaga selectedVaga = vagasTableView.getSelectionModel().getSelectedItem();

        if (selectedVaga != null) {
            // Create the confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Exclusão");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Tem certeza que deseja excluir a vaga selecionada?\nEssa ação não pode ser desfeita.");

            // Show the dialog and wait for the user's response
            Optional<ButtonType> result = alert.showAndWait();

            // Only proceed if the user clicked "OK"
            if (result.isPresent() && result.get() == ButtonType.OK) {
                JsonDataManager dataManager = JsonDataManager.getInstance();
                AppData appData = dataManager.getData();

                System.out.println("Excluding Vaga...");
                appData.removeVaga(selectedVaga);
                dataManager.saveData();
                loadVagas();
            }
        } else {
            // Optional: Show a warning if nothing is selected
            System.out.println("Nenhuma vaga selecionada para exclusão.");
        }
    }
}