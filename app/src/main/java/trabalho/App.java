package trabalho;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This is the main entry point for your JavaFX application.
 */
public class App extends Application {

    // The start() method is the main entry point for all JavaFX applications.
    @Override
    public void start(Stage primaryStage) {
        // A Label is a simple text control.
        Label helloLabel = new Label("Hello, JavaFX! Your HR Management App is running.");

        // A StackPane is a simple layout pane that stacks nodes on top of each other.
        StackPane root = new StackPane();
        root.getChildren().add(helloLabel);

        // A Scene represents the content inside a window.
        // We set the root layout and the size of the window (640x480).
        Scene scene = new Scene(root, 640, 480);

        // The Stage is the main window of the application.
        primaryStage.setTitle("HR Management System");
        primaryStage.setScene(scene);

        // Show the window to the user.
        primaryStage.show();
    }

    // The main() method is not strictly required for JavaFX apps but is good
    // practice.
    // It's used to launch the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }
}