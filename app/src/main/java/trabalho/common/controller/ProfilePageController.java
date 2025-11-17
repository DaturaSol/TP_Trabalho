package trabalho.common.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import trabalho.admin.controller.DashboardAdministradorController;
import trabalho.admin.controller.DashboardGestorController;
import trabalho.admin.model.Usuario;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.recrutamento.controller.MenuRecrutamentoController;

public class ProfilePageController {

    @FXML
    private Button backButton;
    @FXML
    private Button adminButton;
    @FXML
    private Button gestorButton;
    @FXML
    private Button recrutadorButton;
    @FXML
    private Button funcionarioButton;

    private Usuario currentUser;

    public void initData(Usuario user) {
        this.currentUser = user;
        configureRoleButtons();
    }

    /**
     * Checks the roles of the current user and sets the visibility of the
     * corresponding buttons.
     */
    private void configureRoleButtons() {
        if (currentUser == null)
            return; // Safety check

        AppData appData = JsonDataManager.getInstance().getData();
        String cpf = currentUser.getCpfCnpj();

        // Check for Administrator Role
        boolean isAdmin = appData.getAdministradores().containsKey(cpf);
        setButtonVisibility(adminButton, isAdmin);

        // Check for Gestor Role
        boolean isGestor = appData.getGestores().containsKey(cpf);
        setButtonVisibility(gestorButton, isGestor);

        // Check for Recrutador Role
        boolean isRecrutador = appData.getRecrutadores().containsKey(cpf);
        setButtonVisibility(recrutadorButton, isRecrutador);

        // Check for Funcionario Role (general)
        boolean isFuncionario = appData.getFuncionarios().containsKey(cpf);
        setButtonVisibility(funcionarioButton, isFuncionario);
    }

    /**
     * A helper method to correctly show or hide a button.
     * Hiding a button is not enough; you must also tell the layout to ignore it.
     *
     * @param button    The button to modify.
     * @param isVisible True to show the button, false to hide it.
     */
    private void setButtonVisibility(Button button, boolean isVisible) {
        button.setVisible(isVisible);
        button.setManaged(isVisible); // This is crucial! It removes the space of the hidden button.
    }

    // --- Action Handlers for Button Clicks ---

    @FXML
    private void handleAdminButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/dashboardAdministrador.fxml"));
            Parent root = loader.load();

            DashboardAdministradorController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Administrador Dashboard");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao acessar dashboardAdministrador.fxml.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleGestorButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/dashboardGestor.fxml"));
            Parent root = loader.load();

            DashboardGestorController controller = loader.getController();
            controller.initData(this.currentUser);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestor Dashboard");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao acessar dashboardGestor.fxml.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleRecrutadorButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/recrutamento/menu_recrutamento.fxml"));
            Parent root = loader.load();

            MenuRecrutamentoController controller = loader.getController();
            controller.initData(this.currentUser);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Recrutrador Dashboard");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao acessar menu_recrutamento.fxml.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleFuncionarioButtonAction() {
        System.out.println("Funcionario button clicked! Navigating to Funcionario Dashboard...");
        // TODO: Add navigation logic to the funcionario screen
    }

    @FXML
    private void handleBackButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Profile");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao voltar para a tela de login.");
        }
    }
}
