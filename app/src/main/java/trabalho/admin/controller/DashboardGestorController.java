package trabalho.admin.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import trabalho.admin.model.Usuario;
import trabalho.common.controller.ProfilePageController;

// TODO: Import necessary classes for navigation and data handling
// import javafx.stage.Stage;
// import javafx.scene.Scene;
// import java.io.IOException;

/**
 * Controller for the manager dashboard (DashboardGestor.fxml).
 * This class handles user interactions on the manager's dashboard, providing
 * navigation to various management panels like users, vacancies, and
 * applications.
 */
public class DashboardGestorController {

    // <editor-fold desc="FXML UI Components">
    // These variables are linked to the FXML components with the same fx:id.
    // The @FXML annotation tells the FXMLLoader to inject the UI element here.

    @FXML
    private Button backButton;

    @FXML
    private Hyperlink painelUsuariosButton;

    @FXML
    private Hyperlink painelVagasButton;

    @FXML
    private Hyperlink verCandaidaturasButton; // Note: fx:id has a typo "Candaidaturas"

    @FXML
    private Hyperlink aprovarContratacoesButton;

    @FXML
    private Hyperlink editarInfoPessoalButton;
    // </editor-fold>

    private Usuario currentUser;

    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. This is a good place for any
     * initial setup code.
     */
    @FXML
    private void initialize() {
        System.out.println("DashboardGestorController initialized.");
        // TODO: Add any initial setup logic here if needed.
        // For example, disabling certain links based on user permissions.
    }

    // <editor-fold desc="FXML Action Handlers">
    // These methods are called when the corresponding UI element is clicked,
    // as defined by the onAction attribute in the FXML file.

    /**
     * Handles the action of clicking the "Voltar" (Back) button.
     * Should navigate the user to the previous screen (likely the admin dashboard
     * or profile page).
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
     * Handles navigating to the User Panel.
     *
     * @param event The action event.
     */
    @FXML
    private void handlePainelUsuariosButtonAction(ActionEvent event) {
        System.out.println("'Painel de Usuarios' clicked.");
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
     * Handles navigating to the Vacancies Panel.
     *
     * @param event The action event.
     */
    @FXML
    private void handlePainelVagasButtonAction(ActionEvent event) {
        System.out.println("'Painel de Vagas' clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/painelVagas.fxml"));
            Parent root = loader.load();

            PainelVagasController controller = loader.getController();
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
     * Handles navigating to the view where applications can be seen.
     *
     * @param event The action event.
     */
    @FXML
    private void handleVerCandidaturasButtonAction(ActionEvent event) {
        System.out.println("'Ver Candidaturas' clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/trabalho/fxml/admin/pesquisarCandidaturas.fxml"));
            Parent root = loader.load();

            PesquisarCandidaturasController controller = loader.getController();
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
     * Handles navigating to the panel for approving hires.
     *
     * @param event The action event.
     */
    @FXML
    private void handleAprovarContratacoesButtonAction(ActionEvent event) {
        System.out.println("'Aprovar Contratações' clicked.");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/trabalho/fxml/admin/autorizarContratacoes.fxml"));
            Parent root = loader.load();

            AutorizarContratacoesController controller = loader.getController();
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
     * Handles navigating to the personal information editing page.
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