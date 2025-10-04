package trabalho.common.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import trabalho.admin.model.Administrador;
import trabalho.admin.model.Gestor;
import trabalho.admin.model.Usuario;
import trabalho.common.model.Role;
import trabalho.financeiro.model.Funcionario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class JsonDataManagerTest {

    private static final String TEST_JSON_FILE = "test_hr_data.json";

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
        JsonDataManager.resetInstance();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
    }

    @Test
    @DisplayName("Should save, load, modify, and re-save multiple distinct users correctly")
    void testMultipleSaveAndLoadCycles() {
        // --- PHASE 1: CREATE AND SAVE THE INITIAL DATASET ---
        System.out.println("--- testMultipleSaveAndLoadCycles: Phase 1: Creating initial dataset ---");
        JsonDataManager firstManager = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData firstData = firstManager.getData();

        Administrador admin = new Administrador("Clara Admin", "111", "clara@corp.com", "...", 111, "admin", "pass1",
                "SysAdmin", "Active", "IT", 10000);
        Gestor gestor = new Gestor("David Gestor", "222", "david@corp.com", "...", 222, "gestor", "pass2", "Team Lead",
                "Active", "Dev", 12000);

        firstData.savePessoa(admin);
        firstData.savePessoa(gestor);
        firstManager.saveData();

        // --- PHASE 2: LOAD, ADD A NEW FUNCIONARIO, AND RE-SAVE ---
        System.out.println("--- testMultipleSaveAndLoadCycles: Phase 2: Loading, modifying, and re-saving ---");
        JsonDataManager.resetInstance();
        JsonDataManager secondManager = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData secondData = secondManager.getData();
        assertEquals(2, secondData.getPessoas().size(), "Should have loaded 2 people.");

        Funcionario func = new Funcionario("Frank Func", "444", "frank@corp.com", "...", 444, "frank", "pass4",
                Role.FUNCIONARIO, "Dev", "Active", "Dev", 8000);
        secondData.savePessoa(func);
        secondManager.saveData();

        // --- PHASE 3: LOAD THE FINAL STATE AND VERIFY EVERYTHING ---
        System.out.println("--- testMultipleSaveAndLoadCycles: Phase 3: Loading final state for verification ---");
        JsonDataManager.resetInstance();
        JsonDataManager thirdManager = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData finalData = thirdManager.getData();

        assertEquals(3, finalData.getPessoas().size(), "Final dataset should contain 3 people.");
        assertNotNull(finalData.findPessoaByCpfCnpj("111"), "Original Admin should exist.");
        assertNotNull(finalData.findPessoaByCpfCnpj("444"), "Newly added Funcionario should exist.");
        assertTrue(finalData.findUserByLogin("frank").isPresent(), "Index should be updated with new user.");
    }

    @Test
    @DisplayName("Should allow a single user to have multiple roles, which can be added and removed")
    void testUserCanHaveMultipleRolesAndRolesCanBeRemoved() {
        // --- ARRANGE: Create a user and give them multiple roles ---
        System.out.println("--- testUserRoles: Phase 1: Creating user with multiple roles ---");
        JsonDataManager manager1 = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData data1 = manager1.getData();

        Administrador maria = new Administrador("Multi-Role Maria", "777", "maria@corp.com", "...", 777, "maria",
                "pass7", "Lead", "Active", "IT", 15000);
        // The constructor adds ADMIN. Let's add more roles.
        maria.addRole(Role.GESTOR);
        maria.addRole(Role.FUNCIONARIO);
        data1.savePessoa(maria);
        manager1.saveData();

        // --- ACT 1 & ASSERT 1: Reload and verify all roles are present ---
        System.out.println("--- testUserRoles: Phase 2: Verifying initial roles after load ---");
        JsonDataManager.resetInstance();
        JsonDataManager manager2 = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData data2 = manager2.getData();

        Usuario loadedMaria1 = (Usuario) data2.findPessoaByCpfCnpj("777");
        assertNotNull(loadedMaria1);
        Set<Role> roles1 = loadedMaria1.getRoles();
        assertEquals(3, roles1.size(), "Maria should have 3 roles after initial save and load.");
        assertTrue(roles1.contains(Role.ADMIN));
        assertTrue(roles1.contains(Role.GESTOR));
        assertTrue(roles1.contains(Role.FUNCIONARIO));

        // --- ACT 2: Remove a role and re-save ---
        System.out.println("--- testUserRoles: Phase 3: Removing a role and re-saving ---");
        loadedMaria1.removeRole(Role.GESTOR);
        manager2.saveData();

        // --- ACT 3 & ASSERT 2: Reload and verify the role is gone ---
        System.out.println("--- testUserRoles: Phase 4: Verifying final roles after removal ---");
        JsonDataManager.resetInstance();
        JsonDataManager manager3 = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData data3 = manager3.getData();

        Usuario loadedMaria2 = (Usuario) data3.findPessoaByCpfCnpj("777");
        assertNotNull(loadedMaria2);
        Set<Role> roles2 = loadedMaria2.getRoles();
        assertEquals(2, roles2.size(), "Maria should have 2 roles after removal.");
        assertTrue(roles2.contains(Role.ADMIN), "ADMIN role should remain.");
        assertFalse(roles2.contains(Role.GESTOR), "GESTOR role should be gone.");
        assertTrue(roles2.contains(Role.FUNCIONARIO), "USER role should remain.");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when attempting to remove the last role from a user")
    void testRemovingLastRoleThrowsException() {
        // This is a unit test for the business logic, no file saving needed.
        System.out.println("--- testLastRoleInvariant: Verifying role removal constraint ---");

        // ARRANGE: Create a user with only one role
        Funcionario userWithOneRole = new Funcionario("Lone Wolf", "999", "lone@corp.com", "...", 999, "lone", "pass9",
                Role.FUNCIONARIO, "Specialist", "Active", "R&D", 9000);
        assertEquals(1, userWithOneRole.getRoles().size());

        // ACT & ASSERT: Verify that removing the last role throws the expected
        // exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userWithOneRole.removeRole(Role.FUNCIONARIO);
        });

        // Optional: Assert the exception message is what you expect
        String expectedMessage = "A user must have at least one role";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should explain the reason for failure.");
    }
}