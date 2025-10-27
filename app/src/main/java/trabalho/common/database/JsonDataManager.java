package trabalho.common.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages data persistence using a single JSON file.
 * Configured to handle the polymorphic Pessoa hierarchy.
 * 
 * @author Gabriel M.S.O.
 */
public class JsonDataManager {
    private static final String DEFAULT_JSON_FILE = "hr_data.json";
    private static JsonDataManager instance;

    private final String jsonFile;
    private AppData data;
    private final Gson gson;

    private JsonDataManager(String fileName) {
        this.jsonFile = fileName;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        loadData();
    }

    public static synchronized JsonDataManager getInstance() {
        return getInstance(DEFAULT_JSON_FILE);
    }

    public static synchronized JsonDataManager getInstance(String filename) {
        if (instance == null) {
            instance = new JsonDataManager(filename);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public AppData getData() {
        return data;
    }

    /**
     * Loads application data from the JSON file with a robust, clean flow.
     * It handles file not found, empty/invalid files, and parsing errors
     * gracefully.
     */
    private void loadData() {
        if (Files.exists(Paths.get(this.jsonFile))) {
            try (Reader reader = new FileReader(this.jsonFile)) {
                AppData loadedData = gson.fromJson(reader, AppData.class);

                if (loadedData != null && loadedData.getAdministradores() != null) { // At least one ADM must exist
                    // Success!
                    this.data = loadedData;
                    System.out.println("Data loaded successfully from " + this.jsonFile);
                    this.data.rebuildIndexes(); // Rebuild index on successful load
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error reading/parsing JSON file. Starting fresh. Error: " + e.getMessage());
            }
        }

        System.out.println("Initializing a new, empty data set in memory.");
        initializeEmptyData();
    }

    public void saveData() {
        try (Writer writer = new FileWriter(this.jsonFile)) {
            gson.toJson(this.data, writer);
            System.out.println("Data saved to " + this.jsonFile);
        } catch (IOException e) {
            System.err.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    private void initializeEmptyData() {
        this.data = new AppData();
        this.data.rebuildIndexes();
    }
}