package trabalho.admin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import trabalho.admin.model.Usuario;
import trabalho.candidatura.model.Pessoa;
import trabalho.financeiro.model.Funcionario;

// TODO: Import your model classes (Funcionario, Pessoa, Usuario)
// import trabalho.admin.model.Funcionario;
// import trabalho.admin.model.Pessoa;
// import trabalho.admin.model.Usuario;

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

    // For new User creation
    public void initData(Usuario currentUser) {
        this.currentUser = currentUser;
    }
    // For editing existing User
    public void initData(Usuario currentUser, Funcionario funcionario) {
        this.currentUser = currentUser;
        this.funcionarioToEdit = funcionario;
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        System.out.println("EditUsuarioController initialized.");
        // You can add listeners here for real-time validation if needed.
    }

    /**
     * This is the entry point for the controller. It's called from the previous
     * screen
     * to set up the form for either creating or editing a user.
     *
     * @param funcionario The funcionario to edit. If null, the form will be in
     *                    "Create Mode".
     */
    public void initData(Funcionario funcionario) {
        this.funcionarioToEdit = funcionario;

        if (funcionarioToEdit != null) {
            // --- EDIT MODE ---
            title.setText("Editar Usuário");
            populateFields(funcionarioToEdit);
            cpfField.setEditable(false); // The primary key (CPF) should not be editable.
        } else {
            // --- CREATE MODE ---
            // The title is already "Criar Usuario" by default from the FXML.
            // All fields are blank by default.
            title.setText("Criar Novo Usuário");
        }
    }

    /**
     * A helper method to fill all the form fields with data from a Funcionario
     * object.
     * 
     * @param funcionario The user whose data will be displayed.
     */
    private void populateFields(Funcionario funcionario) {
        Pessoa pessoa = funcionario.getPessoa();
        if (pessoa == null)
            return; // Safety check

        cpfField.setText(pessoa.getCpfCnpj());
        nomeField.setText(pessoa.getNome());
        emailField.setText(pessoa.getEmail());
        enderecoField.setText(pessoa.getEndereco());
        telefoneField.setText(String.valueOf(pessoa.getTelefone()));
        // TODO: Populate Cargo, Departamento, and Salario from the Funcionario model
        // CargoField.setText(funcionario.getCargo());
        // departamentoField.setText(funcionario.getDepartamento());
        // salarioField.setText(String.valueOf(funcionario.getSalario()));

        // TODO: Set the checkboxes based on the user's roles
        // For example:
        // administradorButton.setSelected(funcionario.isAdministrador());
        // gestorButton.setSelected(funcionario.isGestor());
    }

    // <editor-fold desc="FXML Action Handlers">

    @FXML
    private void handleSalvarButtonAction(ActionEvent event) {
        // TODO: Add input validation logic here first.
        // - Are required fields empty?
        // - Do passwords match?
        // - Is the email format valid?
        // if (!validateInput()) { return; }

        if (funcionarioToEdit == null) {
            // --- LOGIC TO CREATE A NEW USER ---
            System.out.println("Saving a NEW user...");
            // 1. Gather data from all fields.
            // 2. Create new Pessoa, Usuario, and Funcionario objects.
            // 3. Save them to your data source (JSON file, database, etc.).
            // 4. Show a success message.
            // 5. Navigate back to the user list screen.
        } else {
            // --- LOGIC TO UPDATE AN EXISTING USER ---
            System.out.println("Updating user: " + funcionarioToEdit.getPessoa().getCpfCnpj());
            // 1. Gather data from all fields.
            // 2. Update the properties of the 'funcionarioToEdit' object.
            // 3. Handle password change (only update if the password field is not empty).
            // 4. Save the updated object to your data source.
            // 5. Show a success message.
            // 6. Navigate back to the user list screen.
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button clicked.");
        // TODO: Implement navigation to go back to the previous screen
        // (PainelUsuarios).
    }

    // --- CheckBox Action Handlers ---
    // You can use these methods to enforce rules, e.g., if Admin is checked,
    // then Funcionario must also be checked.

    @FXML
    private void handleAdministradorButtonAction(ActionEvent event) {
        System.out.println("Administrador checkbox toggled.");
    }

    @FXML
    private void handleGestorButtonAction(ActionEvent event) {
        System.out.println("Gestor checkbox toggled.");
    }

    @FXML
    private void handleRecrutadorButtonAction(ActionEvent event) {
        System.out.println("Recrutador checkbox toggled.");
    }

    @FXML
    private void handleFuncionarioButtonAction(ActionEvent event) {
        System.out.println("Funcionario checkbox toggled.");
    }
    // </editor-fold>

    /**
     * A helper method for input validation.
     * 
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInput() {
        // Example validation:
        if (cpfField.getText().isBlank() || nomeField.getText().isBlank()) {
            // TODO: Show an error alert to the user
            System.out.println("Validation Error: CPF and Nome are required.");
            return false;
        }

        if (!senhaField.getText().equals(confirmSenhaField.getText())) {
            // TODO: Show an error alert to the user
            System.out.println("Validation Error: Passwords do not match.");
            return false;
        }

        // Add more validation rules as needed...

        return true;
    }
}