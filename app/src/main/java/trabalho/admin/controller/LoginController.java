package trabalho.admin.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.financeiro.utils.PasswordManager;

import java.io.IOException;
import java.util.Optional;

/*
 * Senhas devem respeitar políticas mínimas (por exemplo: 8 caracteres, letras e números).
 * 
*/

/**
 * Controller for the Login screen (login.fxml).
 * Handles user input and authentication logic.
 */
public class LoginController {

    // 1. These variables are linked to the FXML components with the same fx:id.
    // The @FXML annotation tells the FXMLLoader to inject the UI element here.
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label feedbackLabel;

    /**
     * This method is called when the login button is clicked, as defined by the
     * onAction="#handleLoginButtonAction" in the FXML file.
     */
    @FXML
    protected void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showFeedback("Username cannot be empty.", Color.RED);
            return;
        }
        if (password.isEmpty()) {
            showFeedback("Password cannot be empty.", Color.RED);
            return;
        }

        authenticateUser(username, password);
    }

    /**
     * Checks the user's credentials against the database.
     * 
     * @param login    The username entered by the user.
     * @param passHash The password entered by the user.
     */
    private void authenticateUser(String login, String plainPassword) {
        AppData appData = JsonDataManager.getInstance().getData();

        Optional<Usuario> userOptional = appData.findUserByLogin(login);

        if (userOptional.isPresent()
                && PasswordManager.verifyPassword(plainPassword, userOptional.get().getPassHash())) {
            showFeedback("Login successful!", Color.GREEN);

            navigateToProfilePage(userOptional.get());
        } else {
            showFeedback("Invalid username or password.", Color.RED);
        }
    }

    private void navigateToProfilePage(Usuario user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/common/profile_page.fxml"));
            Parent root = loader.load();

            ProfilePageController controller = loader.getController();
            controller.initData(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showFeedback("Error: Could not load the profile page.", Color.RED);
        }
    }

    /**
     * A helper method to display messages to the user on the feedbackLabel.
     * 
     * @param message The text to display.
     * @param color   The color of the text.
     */
    private void showFeedback(String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setTextFill(color);
    }
}