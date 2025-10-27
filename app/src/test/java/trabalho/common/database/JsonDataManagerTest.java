package trabalho.common.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.recrutamento.model.Vaga;

// Removed imports for Candidato and Funcionario as they are not implemented yet.

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for JsonDataManager and related model actions.
 * This version is modified to only test implemented classes (Administrador,
 * Gestor).
 * To run this with Gradle, execute: ./gradlew test
 * A report will be generated in build/reports/tests/test/index.html
 */
public class JsonDataManagerTest {

    /**
     * This method runs BEFORE EACH @Test method.
     * It deletes the old database file to ensure a clean slate for every test,
     * preventing tests from interfering with each other.
     */
    @BeforeEach
    public void setup() {
        File dbFile = new File("hr_data.json");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        // Reset the singleton instance to force it to re-initialize with an empty state
        JsonDataManager.resetInstance();
    }

    @Test
    public void testAdminCreatesAndDeletesUser() {
        // --- Setup ---
        JsonDataManager dataManager = JsonDataManager.getInstance();
        AppData appData = dataManager.getData();
        Administrador rootAdmin = new Administrador("Root Admin", "000", "root@test.com", "...", 0, "root", "root",
                "SysAdmin", "Active", "IT", 10000);
        rootAdmin.cadastrarUsuario(rootAdmin);

        // --- Action 1: Create a new Gestor ---
        Gestor newGestor = new Gestor("New Gestor", "111", "gestor@test.com", "...", 1, "gestor1", "pass", "Manager",
                "Active", "Sales", 8000);
        rootAdmin.cadastrarUsuario(newGestor);

        // --- Verification 1: Check if the user was created ---
        // Note: listarUsuarios() might not work if Funcionario isn't implemented, so we
        // check specific lists.
        assertEquals(1, appData.getAdministradores().size(), "Administradores list should have 1 entry.");
        assertEquals(1, appData.getGestores().size(), "Gestores list should have 1 entry.");

        // --- Action 2: Delete the Gestor ---
        rootAdmin.excluirUsuario(newGestor);

        // --- Verification 2: Check if the user was deleted ---
        assertEquals(1, appData.getAdministradores().size(), "Administradores list should still have 1 entry.");
        assertTrue(appData.getGestores().isEmpty(), "Gestores list should be empty after deletion.");
    }

    @Test
    public void testGestorFunctionalityAndPermissions() {
        // --- Setup ---
        JsonDataManager dataManager = JsonDataManager.getInstance();
        Administrador admin = new Administrador("Root Admin", "000", "root@test.com", "...", 0, "root", "root",
                "SysAdmin", "Active", "IT", 10000);
        admin.cadastrarUsuario(admin);
        Gestor gestor = new Gestor("Test Gestor", "222", "gestor2@test.com", "...", 2, "gestor2", "pass", "Manager",
                "Active", "HR", 8000);
        admin.cadastrarUsuario(gestor);

        // --- Action 1: Gestor creates a Vaga ---
        Vaga newVaga = new Vaga();
        gestor.criarVaga(newVaga);

        // --- Verification 1 ---
        assertFalse(dataManager.getData().getVagas().isEmpty(), "Vagas list should not be empty after creation.");
        assertEquals(1, dataManager.getData().getVagas().size());

        // --- Action 2 & Verification 2: Gestor tries to delete an Admin ---
        // We assert that this specific action throws the expected exception.
        assertThrows(IllegalArgumentException.class, () -> {
            gestor.excluirUsuario(admin);
        }, "A Gestor should not be able to delete an Administrador.");
    }

    @Test
    public void testDataPersistenceAcrossSessions() {
        // --- Setup Phase 1: Create and save data using ONLY implemented classes ---
        JsonDataManager initialManager = JsonDataManager.getInstance();
        AppData initialData = initialManager.getData();
        initialData.saveAdministrador(
                new Administrador("Persistent Admin", "555", "p@admin.com", "...", 5, "padmin", "p", "a", "s", "d", 1));
        initialData.saveGestor(
                new Gestor("Persistent Gestor", "888", "p@gestor.com", "...", 8, "pgestor", "p", "a", "s", "d", 1));
        initialManager.saveData(); // This writes the data to hr_data.json

        // --- Action: Reset the singleton and create a new instance to force a reload
        // ---
        JsonDataManager.resetInstance();
        JsonDataManager reloadedManager = JsonDataManager.getInstance();
        AppData reloadedData = reloadedManager.getData();

        // --- Verification: Check if the reloaded data matches what was saved ---
        assertFalse(reloadedData.getAdministradores().isEmpty(),
                "Administradores list should not be empty after reload.");
        assertFalse(reloadedData.getGestores().isEmpty(), "Gestores list should not be empty after reload.");

        // Also verify that the unimplemented lists are empty, which is correct
        assertTrue(reloadedData.getFuncionarios().isEmpty(), "Funcionarios list should be empty as none were saved.");
        assertTrue(reloadedData.getCandidatos().isEmpty(), "Candidatos list should be empty as none were saved.");

        assertEquals(1, reloadedData.getAdministradores().size());
        assertEquals(1, reloadedData.getGestores().size());
        assertEquals("Persistent Admin", reloadedData.getAdministradores().get(0).getNome());
        assertEquals("Persistent Gestor", reloadedData.getGestores().get(0).getNome());
    }

    // ======================================================================================
    // === The following tests are commented out until their dependencies are
    // implemented ===
    // ======================================================================================

    /*
     * // TODO: Uncomment this test once the Candidato and Funcionario classes are
     * implemented.
     * // This test is critical for verifying that the database refactor was
     * successful.
     * 
     * @Test
     * public void testCandidatoAndFuncionarioCoexistence() {
     * // --- Setup ---
     * JsonDataManager dataManager = JsonDataManager.getInstance();
     * AppData appData = dataManager.getData();
     * Administrador admin = new Administrador("Root Admin", "000", "root@test.com",
     * "...", 0, "root", "root",
     * "SysAdmin", "Active", "IT", 10000);
     * admin.cadastrarUsuario(admin);
     * 
     * // --- Action 1: Create and save a Candidato ---
     * trabalho.candidatura.model.Candidato candidato = new
     * trabalho.candidatura.model.Candidato("Joana Silva", "333", "joana@email.com",
     * "Rua A", 12345);
     * appData.saveCandidato(candidato);
     * 
     * // --- Action 2: Create and save a Funcionario with the SAME CPF ---
     * trabalho.financeiro.model.Funcionario funcionario = new
     * trabalho.financeiro.model.Funcionario("Joana Silva", "333",
     * "joana.silva@corp.com", "Rua A", 12345, "joana", "pass", null, "Analista",
     * "Active", "Financeiro", 5000);
     * admin.cadastrarUsuario(funcionario);
     * dataManager.saveData(); // Save to be sure
     * 
     * // --- Verification ---
     * assertEquals(1, appData.getCandidatos().size(),
     * "There should be one candidate.");
     * assertEquals(1, appData.getFuncionarios().size(),
     * "There should be one funcionario.");
     * assertEquals("333", appData.getCandidatos().get(0).getCpfCnpj(),
     * "Candidate CPF should match.");
     * assertEquals("333", appData.getFuncionarios().get(0).getCpfCnpj(),
     * "Funcionario CPF should match.");
     * }
     */

    /*
     * // TODO: Uncomment this test once the Funcionario class is implemented.
     * // This test verifies that editing a user updates their data instead of
     * creating a duplicate.
     * 
     * @Test
     * public void testUserEditing() {
     * // --- Setup ---
     * AppData appData = JsonDataManager.getInstance().getData();
     * Administrador admin = new Administrador("Root Admin", "000", "root@test.com",
     * "...", 0, "root", "root",
     * "SysAdmin", "Active", "IT", 10000);
     * admin.cadastrarUsuario(admin);
     * trabalho.financeiro.model.Funcionario userToEdit = new
     * trabalho.financeiro.model.Funcionario("Original Name", "444",
     * "original@email.com", "...", 4, "user4", "pass", null, "Tester", "Active",
     * "QA", 4000);
     * admin.cadastrarUsuario(userToEdit);
     * 
     * // --- Action: Create an "updated" version and edit the user ---
     * trabalho.financeiro.model.Funcionario updatedUser = new
     * trabalho.financeiro.model.Funcionario("Updated Name", "444",
     * "updated@email.com", "...", 4, "user4", "newpass", "Senior Tester", "Active",
     * "QA", 4500);
     * admin.editarUsuario(updatedUser);
     * 
     * // --- Verification ---
     * long userCount = appData.getFuncionarios().stream().filter(f ->
     * f.getCpfCnpj().equals("444")).count();
     * trabalho.financeiro.model.Funcionario finalUser =
     * appData.getFuncionarios().stream().filter(f ->
     * f.getCpfCnpj().equals("444")).findFirst().orElse(null);
     * 
     * assertNotNull(finalUser, "User with CPF 444 should exist.");
     * assertEquals(1, userCount,
     * "There should be only one user with this CPF after editing, not a duplicate."
     * );
     * assertEquals("Updated Name", finalUser.getNome(),
     * "The user's name should have been updated.");
     * }
     */
}