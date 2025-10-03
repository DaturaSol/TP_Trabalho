package trabalho.common.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages data persistence using a single JSON file.
 * This class is a Singleton.
 */
public class JsonDataManager {
    private static final String DEFAULT_JSON_FILE = "hr_data.json";
    private static JsonDataManager instance;

    private final String jsonFile; // In case we want to change the file name
    private AppData data;
    private final Gson gson;

    private JsonDataManager(String fileName) {
        this.jsonFile = fileName;
        // Use GsonBuilder to make the JSON output human-readable
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadData();
    }

    // By default it calls with the DEFAULT_JSON_FILE
    public static synchronized JsonDataManager getInstance() {
        return getInstance(DEFAULT_JSON_FILE);
    }

    public static synchronized JsonDataManager getInstance(String filename) {
        if (instance == null) {
            instance = new JsonDataManager(filename);
        }
        return instance;
    }

    /**
     * <p>
     * <b>DEBUG</b>: Resets the singleton instance. CRITICAL for test isolation.
     * </p>
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    public AppData getData() {
        return data;
    }

    /**
     * <p>
     * <b>NOTE</b>: Only run this function once and access data through:
     * {@code this.data}
     * </p>
     */
    private void loadData() {
        if (Files.exists(Paths.get(this.jsonFile))) {
            try (Reader reader = new FileReader(this.jsonFile)) {
                this.data = gson.fromJson(reader, AppData.class);
                System.out.println("Data loaded from " + this.jsonFile);
            } catch (IOException e) {
                System.err.println("Error reading JSON file: " + e.getMessage());
                // If reading fails, start with a fresh data object
                initializeEmptyData();
            }
        } else {
            System.out.println("JSON file not found. Initializing a new file with default data.");
            initializeEmptyData();
            saveData();
        }
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
    }
}