package trabalho.admin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import trabalho.common.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    private void authenticateUser(String username, String password) {
        // NOTE: This is a basic example. In a real application, you would hash the
        // password!
        String sql = "SELECT role FROM usuarios WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                showFeedback("Login successful! Role: " + role, Color.GREEN);
                // TODO: Here you would navigate to the main application screen based on the
                // user's role.

            } else {
                showFeedback("Invalid username or password.", Color.RED);
            }

        } catch (SQLException e) {
            showFeedback("Database error. Please try again later.", Color.RED);
            e.printStackTrace();
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