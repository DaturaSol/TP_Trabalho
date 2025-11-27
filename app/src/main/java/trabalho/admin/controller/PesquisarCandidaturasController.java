package trabalho.admin.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Candidatura;
import trabalho.candidatura.model.Candidatura.StatusCandidatura;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.financeiro.utils.CpfCnpjManager;

public class PesquisarCandidaturasController {

    // <editor-fold desc="FXML UI Components">
    @FXML
    private Button backButton;
    @FXML
    private TextField idField;
    @FXML
    private ComboBox<StatusCandidatura> statusComboBox;
    @FXML
    private Button pesquisarButton;
    @FXML
    private TableView<Candidatura> candidaturasTableView;
    @FXML
    private TableColumn<Candidatura, String> idColumn;
    @FXML
    private TableColumn<Candidatura, String> cpfColumn;
    @FXML
    private TableColumn<Candidatura, String> statusColumn;
    // </editor-fold>

    private Usuario currentUser;
    private ObservableList<Candidatura> masterCandidaturasList = FXCollections.observableArrayList();

    /**
     * Called by the previous screen to pass necessary data.
     */
    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * This method is called by the FXMLLoader after all @FXML fields have been
     * injected.
     * It's the perfect place for initial setup.
     */
    @FXML
    private void initialize() {
        System.out.println("PesquisarCandidaturasController initialized.");
        setupComboBox();
        setupTableColumns();
        loadInitialData();
    }

    private void setupComboBox() {
        // Populate the ComboBox with all possible status values from the enum
        statusComboBox.getItems().setAll(StatusCandidatura.values());
    }

    private void setupTableColumns() {
        AppData appData = JsonDataManager.getInstance().getData();
        // This is the simplest way to link columns to a model's properties.
        // It requires the Candidatura class to have public getter methods
        // like getVagaId(), getCandidatoCpfCnpj(), and getStatus().
        idColumn.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getVaga().getId();
            return new SimpleStringProperty(id != null ? id : "");
        });
        cpfColumn.setCellValueFactory(cellData -> {
            String cpf = cellData.getValue().getCpfCnpjCandidato();
            var pessoa = appData.getPessoas().get(cpf);
            if (pessoa == null) {
                return new SimpleStringProperty("Candidato não encontrado (CPF: " + cpf + ")");
            }

            String nome = pessoa.getNome();
            return new SimpleStringProperty("Nome: " + nome + " CPF: " + CpfCnpjManager.format(cpf));
        });
        statusColumn.setCellValueFactory(cellData -> {
            StatusCandidatura status = cellData.getValue().getStatusEnum();
            return new SimpleStringProperty(status != null ? status.toString() : "");
        });
    }

    private void loadInitialData() {
        // Load all candidaturas from the data source into our master list
        masterCandidaturasList.setAll(JsonDataManager.getInstance().getData().getCandidaturas());
        // Display the full list in the table initially
        candidaturasTableView.setItems(masterCandidaturasList);
    }

    @FXML
    private void handlePesquisarButtonAction(ActionEvent event) {
        // Use a Java Stream to filter the master list based on the filter criteria
        ObservableList<Candidatura> filteredList = masterCandidaturasList.stream()
                .filter(this::matchesFilters) // Use a helper method for clean logic
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Update the table to show only the filtered results
        candidaturasTableView.setItems(filteredList);
    }

    /**
     * Helper method to check if a single Candidatura object matches the current
     * filters.
     * 
     * @param candidatura The candidatura to check.
     * @return true if it matches, false otherwise.
     */
    private boolean matchesFilters(Candidatura candidatura) {
        String idFilter = idField.getText().trim();
        StatusCandidatura statusFilter = statusComboBox.getValue();

        if (!idFilter.isEmpty() && !String.valueOf(candidatura.getVaga().getId()).contains(idFilter)) {
            return false;
        }

        // Check Status filter (exact match)
        // This is the crucial fix: only perform the check if the user has selected a
        // status.
        if (statusFilter != null && candidatura.getStatusEnum() != statusFilter) {
            return false;
        }

        // If the candidatura passed all checks, it's a match
        return true;
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button to be implemented.");
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
}