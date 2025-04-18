package org.example.cpuschedular;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            // Load FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();

            // Create scene (remove fixed size for better responsiveness)
            Scene scene = new Scene(root);
            stage.setTitle("CPU Scheduler");

            // Load icon (make sure path is correct)
            try {
                InputStream iconStream = HelloApplication.class.getResourceAsStream("calendar.png");
                if (iconStream != null) {
                    Image icon = new Image(iconStream);
                    stage.getIcons().add(icon);
                } else {
                    System.err.println("Icon not found at: calendar.png");
                }
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file:");
            e.printStackTrace();
            throw e; // Re-throw to maintain original behavior
        }
    }

    public static void main(String[] args) {
        launch();
    }
}