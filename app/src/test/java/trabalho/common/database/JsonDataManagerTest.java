package trabalho.common.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trabalho.candidatura.model.Pessoa; // Assuming this is the correct package

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JsonDataManagerTest {

    // Use a dedicated filename for the test database
    private static final String TEST_JSON_FILE = "test_hr_data.json";

    @BeforeEach
    void setUp() throws IOException {
        // This is crucial: delete the test file before each test runs
        // to ensure we start with a clean slate.
        Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Reset the singleton to ensure test isolation
        JsonDataManager.resetInstance();
        // Clean up the created file after the test
        Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
    }

    @Test
    void testSaveData_ShouldWritePessoaToJsonFile() throws IOException {
        // --- ARRANGE ---
        // Get the instance, but point it to our test file
        JsonDataManager dataManager = JsonDataManager.getInstance(TEST_JSON_FILE);
        AppData appData = dataManager.getData();
        Pessoa newPessoa = new Pessoa("João Teste", "123.456.789-00", "joao@teste.com", "Rua Teste", 666);

        // --- ACT ---
        // Add the person and call the method we want to test
        appData.getPessoas().add(newPessoa);
        dataManager.saveData();

        // --- ASSERT ---
        // The test isn't complete until we verify the result.
        // 1. Check if the file was actually created.
        assertTrue(Files.exists(Paths.get(TEST_JSON_FILE)), "The JSON file should have been created.");

        // 2. Read the file's content and check if our data is in it.
        String fileContent = new String(Files.readAllBytes(Paths.get(TEST_JSON_FILE)));

        System.out.println("Content of " + TEST_JSON_FILE + ":\n" + fileContent);

        assertTrue(fileContent.contains("João Teste"), "File content should contain the person's name.");
        assertTrue(fileContent.contains("123.456.789-00"), "File content should contain the person's CPF.");
        assertTrue(fileContent.contains("666"), "File content should contain the person's unique number.");
    }
}