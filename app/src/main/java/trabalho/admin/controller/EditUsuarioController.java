package trabalho.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Pessoa;
import trabalho.common.controller.ProfilePageController;
import trabalho.common.database.AppData;
import trabalho.common.database.JsonDataManager;
import trabalho.exceptions.DuplicateDataException;
import trabalho.exceptions.MissingDataException;
import trabalho.financeiro.model.Funcionario;
import trabalho.financeiro.utils.CpfCnpjManager;
import trabalho.financeiro.utils.PasswordManager;
import trabalho.recrutamento.model.Recrutador;

/**
 * Controller for the user creation/editing screen (EditUsuario.fxml).
 * This controller handles two modes:
 * 1. Create Mode: All fields are blank, for creating a new user.
 * 2. Edit Mode: Fields are pre-populated with an existing user's data.
 */
public class EditUsuarioController {

    // <editor-fold desc="FXML UI Components">
    @FXML
    private Button backButton;
    @FXML
    private Label title;
    @FXML
    private Label feedbackLabel;

    // --- User Data Fields ---
    @FXML
    private TextField cpfField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private PasswordField confirmSenhaField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField enderecoField;
    @FXML
    private TextField telefoneField;
    @FXML
    private TextField CargoField; // Note: FXML has 'C' capitalized
    @FXML
    private TextField departamentoField;
    @FXML
    private TextField salarioField;

    // --- Access Profile CheckBoxes ---
    @FXML
    private CheckBox administradorButton;
    @FXML
    private CheckBox gestorButton;
    @FXML
    private CheckBox recrutadorButton;
    // Note: FXML has a typo in the fx:id ("funcitonarioButton")
    @FXML
    private CheckBox funcitonarioButton;

    // --- Action Button ---
    @FXML
    private Button salvarButton;
    // </editor-fold>

    // This variable will hold the user being edited. It will be null if we are
    // creating a new user.
    private Funcionario funcionarioToEdit;
    private Usuario currentUser;
    private List<CheckBox> roleCheckBoxes;

    private List<TextInputControl> requiredFields;
    private static final String STYLE_ERROR = "-fx-border-color: red; -fx-background-color: #ffdddd;";

    // For new User creation
    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
        title.setText("Criar Novo Usuário");
    }

    // For editing existing User
    public void initData(Usuario currentUser, Funcionario funcionario) {
        this.currentUser = currentUser;
        this.funcionarioToEdit = funcionario;
        title.setText("Editar Usuário");
        populateFields(funcionarioToEdit);
        cpfField.setEditable(false); // The primary key (CPF) should not be editable.
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        System.out.println("EditUsuarioController initialized.");
        roleCheckBoxes = List.of(administradorButton, gestorButton, recrutadorButton, funcitonarioButton);

        requiredFields = List.of(cpfField, senhaField, confirmSenhaField,
                CargoField, departamentoField, salarioField);

        for (TextInputControl field : requiredFields) {
            field.textProperty().addListener((obs, oldText, newText) -> {
                // When text changes, remove the error style.
                field.setStyle("");
            });
        }
    }

    /**
     * A helper method to fill all the form fields with data from a Funcionario
     * object.
     * 
     * @param funcionario The user whose data will be displayed.
     */
    private void populateFields(Funcionario funcionario) {
        AppData appData = JsonDataManager.getInstance().getData();
        Pessoa pessoa = funcionario.getPessoa(appData);
        if (pessoa == null)
            return;

        cpfField.setText(pessoa.getCpfCnpj());
        nomeField.setText(pessoa.getNome());
        emailField.setText(pessoa.getEmail());
        enderecoField.setText(pessoa.getEndereco());
        telefoneField.setText(String.valueOf(pessoa.getTelefone()));
        CargoField.setText(funcionario.getCargo());
        departamentoField.setText(funcionario.getDepartamento());
        salarioField.setText(String.valueOf(funcionario.getSalarioBase()));

        switch (funcionario) {
            case Administrador a -> administradorButton.setSelected(true);
            case Gestor g -> gestorButton.setSelected(true);
            case Recrutador r -> recrutadorButton.setSelected(true);
            default -> funcitonarioButton.setSelected(true);
        }
    }

    // <editor-fold desc="FXML Action Handlers">

    @FXML
    private void handleSalvarButtonAction(ActionEvent event) {
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();

        feedbackLabel.setText("");

        if (!CpfCnpjManager.isValid(cpfField.getText())) {
            showError("CPF/CNPJ inválido.", cpfField);
            cpfField.setStyle(STYLE_ERROR);
            return;
        }

        // New user must provide a password
        if (this.funcionarioToEdit == null && senhaField.getText().isBlank()) {
            showError("Senha é obrigatória para novo usuário.", senhaField);
            return;
        }

        // If there is a password, validation for new or changed password
        if (!senhaField.getText().isBlank() && !PasswordManager.isPasswordValid(senhaField.getText())) {
            Set<PasswordManager.PasswordViolations> violations = PasswordManager
                    .getPasswordViolations(senhaField.getText());
            showError(PasswordManager.formatViolations(violations), senhaField);
            return;
        }

        // Passwords must match
        if (!senhaField.getText().equals(confirmSenhaField.getText())) {
            showError("As senhas não coincidem.", confirmSenhaField);
            return;
        }

        if (CargoField.getText().isBlank()) {
            showError("Cargo é obrigatório.", CargoField);
            return;
        }

        if (departamentoField.getText().isBlank()) {
            showError("Departamento é obrigatório.", departamentoField);
            return;
        }

        String userPassHash;
        // Existing user keeps current password if field is blank
        if (this.funcionarioToEdit != null && senhaField.getText().isBlank()) {
            userPassHash = this.funcionarioToEdit.getUsuario(appData).getPassHash();
        } else {
            userPassHash = PasswordManager.hashPassword(senhaField.getText());
        }

        long telefone = 0;

        try {
            if (!telefoneField.getText().isBlank()) {
                telefone = Long.parseLong(telefoneField.getText());
            }
        } catch (NumberFormatException e) {
            showError("Telefone inválido. Insira apenas números.", telefoneField);
            return;
        }

        double salario;
        if (salarioField.getText().isBlank()) {
            showError("Salário é obrigatório.", salarioField);
            return;
        }
        try {
            salario = Double.parseDouble(salarioField.getText());
        } catch (NumberFormatException e) {
            showError("Salário inválido. Insira um número válido.", salarioField);
            return;
        }

        if (roleCheckBoxes.stream().noneMatch(CheckBox::isSelected)) {
            showError("Selecione ao menos um perfil de acesso.", null);
            return;
        }

        String formattedCpfCnpj = CpfCnpjManager.toOnlyNumbers(cpfField.getText());
        Pessoa pessoa = new Pessoa(
                formattedCpfCnpj,
                nomeField.getText(),
                emailField.getText(),
                enderecoField.getText(),
                telefone);

        Usuario usuario = new Usuario(
                formattedCpfCnpj,
                userPassHash);

        try {
            appData.addPessoa(pessoa);
        } catch (DuplicateDataException e1) {
            appData.removePessoa(pessoa);
            try {
                appData.addPessoa(pessoa);
            } catch (DuplicateDataException e2) {
                // Should never happen
                e2.printStackTrace();
            }
        }
        try {
            appData.addUsuario(usuario);
        } catch (DuplicateDataException e) {
            appData.removeUsuario(usuario);
            try {
                appData.addUsuario(usuario);
            } catch (DuplicateDataException | MissingDataException e1) {
                // Should never happen
                e1.printStackTrace();
            }
        } catch (MissingDataException e) {
            // Should never happen
            e.printStackTrace();
        }

        Funcionario funcionario;
        if (administradorButton.isSelected()) {
            funcionario = new Administrador(
                    formattedCpfCnpj,
                    CargoField.getText(),
                    "Ativo",
                    departamentoField.getText(),
                    salario);

        } else if (gestorButton.isSelected()) {
            funcionario = new Gestor(
                    formattedCpfCnpj,
                    CargoField.getText(),
                    "Ativo",
                    departamentoField.getText(),
                    salario);

        } else if (recrutadorButton.isSelected()) {
            funcionario = new Recrutador(
                    formattedCpfCnpj,
                    CargoField.getText(),
                    "Ativo",
                    departamentoField.getText(),
                    salario);

        } else if (funcitonarioButton.isSelected()) {
            funcionario = new Funcionario(
                    formattedCpfCnpj,
                    CargoField.getText(),
                    "Ativo",
                    departamentoField.getText(),
                    salario);

        } else {
            funcionario = new Funcionario();
            feedbackLabel.setTextFill(Color.RED);
            feedbackLabel.setText("Selecione ao menos um perfil de acesso.");
            return;
        }

        try {
            appData.addFuncionario(funcionario);
        } catch (DuplicateDataException e) {
            if (funcionarioToEdit == null) { // If we are not editing show error
                showError(e.getMessage(), null);
            } else {
                appData.removeFuncionario(funcionario); // If we are editing delete
                try {
                    appData.addFuncionario(funcionario);
                } catch (DuplicateDataException | MissingDataException e1) {
                    // Should never happen
                    e1.printStackTrace();
                }
            }
        } catch (MissingDataException e) {
            // Should never happen
            e.printStackTrace();
        } finally {
            dataManager.saveData();
        }
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

    private boolean isCurrentUserAnAdmin() {
        // Make sure currentUser is not null before checking
        if (this.currentUser == null) {
            return false;
        }
        return JsonDataManager.getInstance().getData()
                .getAdministradores().containsKey(this.currentUser.getCpfCnpj());
    }

    @FXML
    private void handleRoleSelection(ActionEvent event) {
        CheckBox selectedCheckbox = (CheckBox) event.getSource();

        if (selectedCheckbox == administradorButton && selectedCheckbox.isSelected()) {
            if (!isCurrentUserAnAdmin()) {
                selectedCheckbox.setSelected(false);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Permissão Negada");
                alert.setHeaderText("Não é possível selecionar o cargo de Administrador.");
                alert.setContentText("Apenas um administrador existente pode atribuir este cargo.");
                alert.showAndWait();

                return;
            }
        }

        // If a checkbox was selected, deselect all others.
        if (selectedCheckbox.isSelected()) {
            for (CheckBox cb : roleCheckBoxes) {
                if (cb != selectedCheckbox) {
                    cb.setSelected(false);
                }
            }
        } else {
            // This logic prevents the user from having no role selected.
            // When they uncheck a box, it re-selects it.
            selectedCheckbox.setSelected(true);
        }

        System.out.println(selectedCheckbox.getText() + " role selected on UI.");
    }

    private void showError(String message, TextInputControl fieldToHighlight) {
        feedbackLabel.setTextFill(Color.RED);
        feedbackLabel.setText(message);
        if (fieldToHighlight != null) {
            fieldToHighlight.setStyle(STYLE_ERROR);
        }
    }

}