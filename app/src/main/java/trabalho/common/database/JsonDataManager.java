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
    private static final String JSON_FILE = "hr_data.json";
    private static JsonDataManager instance;
    private AppData data;
    private final Gson gson;

    private JsonDataManager() {
        // Use GsonBuilder to make the JSON output human-readable
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadData();
    }

    public static synchronized JsonDataManager getInstance() {
        if (instance == null) {
            instance = new JsonDataManager();
        }
        return instance;
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
        if (Files.exists(Paths.get(JSON_FILE))) {
            try (Reader reader = new FileReader(JSON_FILE)) {
                this.data = gson.fromJson(reader, AppData.class);
                System.out.println("Data loaded from " + JSON_FILE);
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
        try (Writer writer = new FileWriter(JSON_FILE)) {
            gson.toJson(this.data, writer);
            System.out.println("Data saved to " + JSON_FILE);
        } catch (IOException e) {
            System.err.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    private void initializeEmptyData() {
        this.data = new AppData();
    }
}