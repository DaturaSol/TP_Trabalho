package trabalho.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Pessoa;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.exceptions.DuplicateDataException;
import trabalho.financeiro.utils.PasswordManager; // Assuming this is the correct path

/**
 * Controller for the Personal Information screen (InfoPessoal.fxml).
 * Allows the currently logged-in user to view and update their own details.
 */
public class InfoPessoalController {

    // <editor-fold desc="FXML UI Components">
    @FXML
    private Button backButton;
    @FXML
    private Button salvarButton;
    @FXML
    private TextField cpfField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private PasswordField confirmarSenhaField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField enderecoField;
    @FXML
    private TextField telefoneField;
    @FXML
    private Label feedbackLabel;
    // </editor-fold>

    private Usuario currentUser; // The user whose information is being edited
    private List<TextInputControl> requiredFields;

    private static final String STYLE_ERROR = "-fx-border-color: red; -fx-background-color: #ffdddd;";
    // private static final String STYLE_SUCCESS = "-fx-border-color: green;
    // -fx-background-color: #ddffdd;";

    @FXML
    private void initialize() {
        System.out.println("InfoPessoalController initialized.");

        // Define which fields are required. Note: Password fields are not in this list
        // because they are optional (user might not want to change their password).
        requiredFields = List.of(nomeField, emailField, enderecoField, telefoneField);

        // Add listeners to automatically clear error styles on input
        for (TextInputControl field : requiredFields) {
            field.textProperty().addListener((obs, oldVal, newVal) -> field.setStyle(""));
        }
        senhaField.textProperty().addListener((obs, oldVal, newVal) -> senhaField.setStyle(""));
        confirmarSenhaField.textProperty().addListener((obs, oldVal, newVal) -> confirmarSenhaField.setStyle(""));
    }

    /**
     * Initializes the controller with the data of the currently logged-in user.
     * This method should be called from the previous screen.
     *
     * @param user The user to be edited.
     */
    public void initData(Usuario user) {
        this.currentUser = user;
        populateFields();
    }

    /**
     * Fills the form fields with the current user's data.
     */
    private void populateFields() {
        AppData appData = JsonDataManager.getInstance().getData();
        if (currentUser == null || currentUser.getPessoa(appData) == null) {
            System.err.println("Error: Cannot populate fields because user or person data is null.");
            return;
        }

        Pessoa pessoa = currentUser.getPessoa(appData);
        cpfField.setText(pessoa.getCpfCnpj());
        cpfField.setEditable(false); // CPF is a primary key and should not be changed.

        nomeField.setText(pessoa.getNome());
        emailField.setText(pessoa.getEmail());
        enderecoField.setText(pessoa.getEndereco());
        telefoneField.setText(String.valueOf(pessoa.getTelefone()));
    }

    @FXML
    private void handleSalvarButtonAction(ActionEvent event) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        if (!validateInput()) {
            return; // Stop if validation fails
        }

        // --- If validation succeeds, proceed to update the data ---
        Pessoa pessoa = currentUser.getPessoa(appData);
        pessoa.setNome(nomeField.getText());
        pessoa.setEmail(emailField.getText());
        pessoa.setEndereco(enderecoField.getText());
        pessoa.setTelefone(Long.parseLong(telefoneField.getText())); // Be careful with parsing

        // **Password Update Logic**: Only update the password if the user has typed a
        // new one.
        if (!senhaField.getText().isEmpty()) {
            String newHashedPassword = PasswordManager.hashPassword(senhaField.getText());
            currentUser.setPassHash(newHashedPassword);
            System.out.println("Password has been updated.");
        }

        appData.removePessoa(pessoa);
        try {
            appData.addPessoa(pessoa);
        } catch (DuplicateDataException e) {
            // Should never happen
            e.printStackTrace();
        }
        dataManager.saveData();
        System.out.println("User information saved successfully for: " + currentUser.getCpfCnpj());

        feedbackLabel.setText("Informações salvas com sucesso!");
        feedbackLabel.setTextFill(Color.GREEN);
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

    /**
     * Validates all user inputs.
     * 
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInput() {
        boolean allValid = true;

        // 1. Check required fields
        for (TextInputControl field : requiredFields) {
            if (field.getText().isBlank()) {
                field.setStyle(STYLE_ERROR);
                allValid = false;
            } else {
                field.setStyle("");
            }
        }

        // 2. Check password fields if they are being used
        String password = senhaField.getText();
        String confirmPassword = confirmarSenhaField.getText();

        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            // Passwords must match
            if (!password.equals(confirmPassword)) {
                feedbackLabel.setText("As senhas não coincidem.");
                senhaField.setStyle(STYLE_ERROR);
                confirmarSenhaField.setStyle(STYLE_ERROR);
                return false; // Fail fast
            }

            // Password must meet strength requirements
            Set<PasswordManager.PasswordViolations> violations = PasswordManager.getPasswordViolations(password);
            if (!violations.isEmpty()) {
                feedbackLabel.setText(PasswordManager.formatViolations(violations));
                senhaField.setStyle(STYLE_ERROR);
                confirmarSenhaField.setStyle(STYLE_ERROR);
                return false; // Fail fast
            }
        }

        // If other fields were invalid, set a generic message
        if (!allValid) {
            feedbackLabel.setText("Por favor, preencha todos os campos obrigatórios.");
            feedbackLabel.setTextFill(Color.RED);
        } else {
            feedbackLabel.setText(""); // Clear feedback if everything is okay
        }

        return allValid;
    }
}