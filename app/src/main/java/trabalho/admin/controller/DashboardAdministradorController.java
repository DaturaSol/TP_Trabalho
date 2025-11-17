package trabalho.admin.controller;

import java.io.IOException;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import trabalho.admin.model.Administrador;
import trabalho.admin.model.Usuario;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.JsonDataManager;

// TODO: Import necessary classes for navigation and data handling
// import javafx.stage.Stage;
// import javafx.scene.Scene;
// import java.io.IOException;

/**
 * Controller for the administrator dashboard (DashboardAdministrador.fxml).
 * This class handles user interactions on the dashboard, such as navigation
 * and displaying system-wide statistics.
 */
public class DashboardAdministradorController {

    // <editor-fold desc="FXML UI Components">
    // These variables are linked to the FXML components with the same fx:id.
    // The @FXML annotation tells the FXMLLoader to inject the UI element here.

    // --- Top Bar ---
    @FXML
    private Button backButton;

    // --- Left Navigation Panel ---
    @FXML
    private Hyperlink actAsGestorButton;

    @FXML
    private Hyperlink actAsRecutadorButton;

    @FXML
    private Hyperlink painelUsuariosButton;

    @FXML
    private Hyperlink editarInfoPessoalButton;

    // --- Center Content (Report Labels) ---
    @FXML
    private Label totalFuncionarios;

    @FXML
    private Label totalAdministradores;

    @FXML
    private Label totalGestores;

    @FXML
    private Label totalCandidatos;
    // </editor-fold>

    private Usuario currentUser;

    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It's the perfect place to set up
     * the initial state of the view, such as loading and displaying the report
     * data.
     */
    @FXML
    private void initialize() {
        loadReportData();
        System.out.println("DashboardAdministradorController initialized.");
    }

    /**
     * A helper method to populate the report labels with data.
     * This would typically fetch information from a data source.
     */
    private void loadReportData() {
        Map<String, Integer> relatorio = new Administrador()
                .gerarRelatorioGestao(JsonDataManager.getInstance().getData());
        // Placeholder data. Replace with actual data fetching logic.
        int funcCount = relatorio.get("Total de Funcionários"); // e.g., Database.getFuncionarioCount();
        int adminCount = relatorio.get("Administradores"); // e.g., Database.getAdminCount();
        int gestorCount = relatorio.get("Gestores"); // e.g., Database.getGestorCount();
        int candCount = relatorio.get("Total de Candidatos"); // e.g., Database.getCandidatoCount();

        totalFuncionarios.setText("Total de Funcionarios: " + funcCount);
        totalAdministradores.setText("Administradores: " + adminCount);
        totalGestores.setText("Gestores: " + gestorCount);
        totalCandidatos.setText("Candidatos: " + candCount);
    }

    // <editor-fold desc="FXML Action Handlers">
    // These methods are called when the corresponding UI element is clicked,
    // as defined by the onAction attribute in the FXML file.

    /**
     * Handles the action of clicking the "Voltar" (Back) button.
     * Should navigate the user to the previous screen.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
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

    /**
     * Handles the action of clicking the "Atuar como Gestor" hyperlink.
     * Should switch the view to the manager's dashboard.
     *
     * @param event The action event.
     */
    @FXML
    private void handleActAsGestorButtonAction(ActionEvent event) {
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

    /**
     * Handles the action of clicking the "Atuar como Recrutador" hyperlink.
     * Should switch the view to the recruiter's dashboard.
     *
     * @param event The action event.
     */
    @FXML
    private void handleActAsRecrutadorButtonAction(ActionEvent event) {
        System.out.println("'Act as Recrutador' clicked.");
        // TODO: Implement logic to navigate to the Recrutador dashboard.
    }

    /**
     * Handles the action of clicking the "Painel de Usuarios" hyperlink.
     * Should navigate to the user management screen.
     *
     * @param event The action event.
     */
    @FXML
    private void handlePainelUsuariosButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/painelUsuarios.fxml"));
            Parent root = loader.load();

            PainelUsuariosController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Painel de Usuarios");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao acessar painelUsuarios.fxml.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of clicking the "Editar Informações Pessoais" hyperlink.
     * Should navigate to the screen for editing the admin's own profile.
     *
     * @param event The action event.
     */
    @FXML
    private void handleEditarInfoPessoalButtonAction(ActionEvent event) {
        System.out.println("'Editar Informações Pessoais' clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/infoPessoal.fxml"));
            Parent root = loader.load();

            InfoPessoalController controller = loader.getController();
            controller.initData(this.currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Painel de Usuarios");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao acessar painelUsuarios.fxml.\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    // </editor-fold>
}