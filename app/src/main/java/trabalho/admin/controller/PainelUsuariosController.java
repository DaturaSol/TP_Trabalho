package trabalho.admin.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;

import javafx.beans.property.SimpleDoubleProperty;
import trabalho.admin.model.Administrador;
import trabalho.admin.model.Usuario;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.financeiro.model.Funcionario;
import trabalho.financeiro.utils.CpfCnpjManager;

/**
 * Controller for the user management panel (PainelUsuarios.fxml).
 * This class handles displaying, searching, creating, editing, and deleting
 * users.
 */
public class PainelUsuariosController {

    @FXML
    private Button backButton;

    @FXML
    private Button novoUsuarioButton;

    @FXML
    private Button editarUsuarioButton;

    @FXML
    private Button excluirUsuarioButton;

    // --- Left Search/Filter Fields ---
    @FXML
    private TextField cpfField;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField enderecoField;

    @FXML
    private TextField telefoneField;

    @FXML
    private TextField cargoField;

    @FXML
    private TextField departamentoField;

    @FXML
    private TextField salarioField;

    @FXML
    private Button pesquisarButton;

    // --- Center TableView ---
    // IMPORTANT: You should add fx:id="userTableView" to your TableView in the FXML
    // for this variable to be injected correctly.
    @FXML
    private TableView<Funcionario> userTableView;

    @FXML
    private TableColumn<Funcionario, String> cpfColumn;

    @FXML
    private TableColumn<Funcionario, String> nomeColumn;

    @FXML
    private TableColumn<Funcionario, String> emailColumn;

    @FXML
    private TableColumn<Funcionario, String> enderecoColumn;

    @FXML
    private TableColumn<Funcionario, String> telefoneColumn;

    @FXML
    private TableColumn<Funcionario, String> cargoColumn;

    @FXML
    private TableColumn<Funcionario, String> departamentoColumn;

    @FXML
    private TableColumn<Funcionario, Double> salarioColumn;

    // An ObservableList to hold the user data for the table.
    private ObservableList<Funcionario> funcionariosList = FXCollections.observableArrayList();
    // </editor-fold>
    private Usuario currentUser;

    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It's used to set up the table columns
     * and load the initial user data.
     */
    @FXML
    private void initialize() {
        System.out.println("PainelUsuariosController initialized.");

        configureTableColumns();

        loadFuncionarios();

        userTableView.setItems(funcionariosList);
        setupTableSelectionListener();
    }

    private void configureTableColumns() {
        AppData appData = JsonDataManager.getInstance().getData();

        cpfColumn.setCellValueFactory(cellData -> {
            String cpf = CpfCnpjManager.format(cellData.getValue().getCpfCnpj());
            return new SimpleStringProperty(cpf != null ? cpf : "");
        });

        nomeColumn.setCellValueFactory(cellData -> {
            String nome = cellData.getValue().getPessoa(appData).getNome();
            return new SimpleStringProperty(nome != null ? nome : "");
        });

        emailColumn.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getPessoa(appData).getEmail();
            return new SimpleStringProperty(email != null ? email : "");
        });

        enderecoColumn.setCellValueFactory(cellData -> {
            String endereco = cellData.getValue().getPessoa(appData).getEndereco();
            return new SimpleStringProperty(endereco != null ? endereco : "");
        });

        telefoneColumn.setCellValueFactory(cellData -> {
            long telefone = cellData.getValue().getPessoa(appData).getTelefone();
            return new SimpleStringProperty(telefone != 0 ? String.valueOf(telefone) : "");
        });

        cargoColumn.setCellValueFactory(cellData -> {
            String cargo = cellData.getValue().getCargo();
            return new SimpleStringProperty(cargo != null ? cargo : "");
        });

        departamentoColumn.setCellValueFactory(cellData -> {
            String departamento = cellData.getValue().getDepartamento();
            return new SimpleStringProperty(departamento != null ? departamento : "");
        });

        salarioColumn.setCellValueFactory(cellData -> {
            Double salario = cellData.getValue().getSalarioBase();
            return new SimpleDoubleProperty(salario != null ? salario : 0.0).asObject();
        });
    }

    /**
     * A helper method to fetch user data from a database or file
     * and populate the TableView.
     */
    private void loadFuncionarios() {
        AppData appData = JsonDataManager.getInstance().getData();
        funcionariosList.setAll(appData.getAllFuncionarios());
        userTableView.setItems(funcionariosList);
    }

    /**
     * Sets up a listener on the table's selection model to enable or disable
     * the 'Editar' and 'Excluir' buttons.
     */
    private void setupTableSelectionListener() {
        // Initially disable the buttons
        editarUsuarioButton.setDisable(true);
        excluirUsuarioButton.setDisable(true);

        userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isItemSelected = newSelection != null;
            editarUsuarioButton.setDisable(!isItemSelected);
            excluirUsuarioButton.setDisable(!isItemSelected);
        });
    }

    @FXML
    private void handlePesquisarButtonAction(ActionEvent event) {
        AppData appData = JsonDataManager.getInstance().getData();

        System.out.println("'Pesquisar' button clicked.");
        String cpfFilter = cpfField.getText().trim();
        String nomeFilter = nomeField.getText().trim().toLowerCase();
        String emailFilter = emailField.getText().trim().toLowerCase();
        String enderecoFilter = enderecoField.getText().trim().toLowerCase();
        String telefoneFilter = telefoneField.getText().trim();
        String cargoFilter = cargoField.getText().trim().toLowerCase();
        String departamentoFilter = departamentoField.getText().trim().toLowerCase();
        String salarioFilter = salarioField.getText().trim();
        if (cpfFilter.isEmpty() && nomeFilter.isEmpty() && emailFilter.isEmpty()
                && enderecoFilter.isEmpty() && telefoneFilter.isEmpty() && cargoFilter.isEmpty()
                && departamentoFilter.isEmpty() && salarioFilter.isEmpty()) {
            userTableView.setItems(funcionariosList);
            return;
        }

        ObservableList<Funcionario> filteredList = FXCollections.observableArrayList();
        for (Funcionario f : funcionariosList) {
            boolean matchesCpf = cpfFilter.isEmpty()
                    || CpfCnpjManager.toOnlyNumbers(f.getCpfCnpj()).contains(cpfFilter);

            boolean matchesNome = nomeFilter.isEmpty()
                    || f.getPessoa(appData).getNome().toLowerCase().contains(nomeFilter);

            boolean matchesEmail = emailFilter.isEmpty()
                    || f.getPessoa(appData).getEmail().toLowerCase().contains(emailFilter);

            boolean matchesEndereco = enderecoFilter.isEmpty()
                    || f.getPessoa(appData).getEndereco().toLowerCase().contains(enderecoFilter);

            boolean matchesTelefone = telefoneFilter.isEmpty()
                    || String.valueOf(f.getPessoa(appData).getTelefone()).contains(telefoneFilter);

            boolean matchesCargo = cargoFilter.isEmpty() || f.getCargo().toLowerCase().contains(cargoFilter);

            boolean matchesDepartamento = departamentoFilter.isEmpty()
                    || f.getDepartamento().toLowerCase().contains(departamentoFilter);

            boolean matchesSalario = salarioFilter.isEmpty()
                    || String.valueOf(f.getSalarioBase()).contains(salarioFilter);

            if (matchesCpf && matchesNome && matchesEmail && matchesEndereco && matchesTelefone
                    && matchesCargo && matchesDepartamento && matchesSalario) {
                filteredList.add(f);
            }
        }
        userTableView.setItems(filteredList);
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/common/profile_page.fxml"));
            Parent root = loader.load();

            ProfilePageController controller = loader.getController();
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

    @FXML
    private void handleNovoUsuarioButtonAction(ActionEvent event) {
        System.out.println("'Novo Usuario' button clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/editUsuario.fxml"));
            Parent root = loader.load();

            EditUsuarioController controller = loader.getController();
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

    @FXML
    private void handleEditarUsuarioButtonAction(ActionEvent event) {
        System.out.println("'Editar Usuario' button clicked.");
        Funcionario selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser instanceof Administrador && !JsonDataManager.getInstance().getData()
                .getAdministradores().containsKey(this.currentUser.getCpfCnpj())) {
            System.out.println("Only administrators can edit other administrators.");
            return;
        }

        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/editUsuario.fxml"));
                Parent root = loader.load();

                EditUsuarioController controller = loader.getController();
                controller.initData(this.currentUser, selectedUser);

                Stage stage = (Stage) backButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("User Profile");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirUsuarioButtonAction(ActionEvent event) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        System.out.println("'Excluir Usuario' button clicked.");
        Funcionario selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            if (selectedUser instanceof Administrador
                    && !appData.getAdministradores().containsKey(this.currentUser.getCpfCnpj())) {
                System.out.println("Only administrators can delete other administrators.");
                return;
            }
            appData.removeFuncionario(selectedUser);
            dataManager.saveData();
            loadFuncionarios();
        }
    }

}