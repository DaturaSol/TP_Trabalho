package trabalho.admin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import trabalho.admin.model.Usuario;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;

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

        if (username.isEmpty() || password.isEmpty()) {
            showFeedback("Username and password cannot be empty.", Color.RED);
            return;
        }

        // This is a placeholder for a real authentication check.
        authenticateUser(username, password);
    }

    /**
     * Checks the user's credentials against the database.
     * 
     * @param login The username entered by the user.
     * @param passHash The password entered by the user.
     */
    private void authenticateUser(String login, String passHash) {
        // 1. Get the data manager instance
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        // 2. Search the list of users in memory using Java Streams
        Optional<Usuario> userOptional = appData.getUsuarios().stream()
                .filter(user -> user.getLogin().equals(login) && user.getPassHash().equals(passHash))
                .findFirst();

        // 3. Check if a user was found
        if (userOptional.isPresent()) {
            Usuario user = userOptional.get();
            showFeedback("Login successful! Role: " + user.getRoles(), Color.GREEN);
            // TODO: Navigate to the main application screen
        } else {
            showFeedback("Invalid username or password.", Color.RED);
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