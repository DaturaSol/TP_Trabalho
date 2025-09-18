package trabalho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import trabalho.common.database.DatabaseManager;

import java.io.IOException;

/**
 * The main entry point for the HR Management application.
 * This class is responsible for launching the JavaFX application and displaying
 * the initial login screen.
 */
public class App extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * 
     * @param primaryStage The primary window for this application, onto which the
     *                     application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Create an FXMLLoader. This object is responsible for loading the FXML
            // file.
            // The path starts with "/" which means it looks in the root of the 'resources'
            // folder.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/admin/login.fxml"));
            // 2. Load the FXML file. This creates the entire scene graph (all the UI
            // elements) in memory.
            Parent root = loader.load();

            // 3. Create a new Scene to hold the loaded UI.
            Scene scene = new Scene(root);

            // 4. Configure the main window (the Stage).
            primaryStage.setTitle("HR Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); // Optional: makes the window size fixed.

            // 5. Show the window to the user.
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load the login screen FXML.");
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the application is closed.
     * It's a good place to close the database connection.
     */
    @Override
    public void stop() {
        System.out.println("Closing application and database connection.");
        DatabaseManager.closeConnection();
    }

    /**
     * The main method, used to launch the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}