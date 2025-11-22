package trabalho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trabalho.common.database.JsonDataManager;

import java.io.IOException;


public class App extends Application {


    @Override
    public void start(Stage primaryStage) {
        try {
            
            JsonDataManager dataManager = JsonDataManager.getInstance();
            dataManager.saveData(); 


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/trabalho/fxml/candidatura/inicio.fxml"));

            Parent root = loader.load();


            Scene scene = new Scene(root);

            primaryStage.setTitle("HR Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); 

            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load the login screen FXML.");
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        System.out.println("Closing application and saving data...");
        JsonDataManager.getInstance().saveData();
    }


    public String getGreeting() {
        return "The app is working";
    }


    public static void main(String[] args) {
        launch(args);
    }
}