package com.michaelmckibbin.imageanalysis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

/**
 * Main application class for the Image Analysis program.
 *
 * @author Michael McKibbin (20092733)
 * @version 1.0 (2024-02-20)
 */
public class ImageAnalysisApplication extends Application {
@Override
public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ImageAnalysisApplication.class.getResource("imageAnalysis-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load());

    // Add CSS to the scene
    String css = ImageAnalysisApplication.class.getResource("styles.css").toExternalForm();
    scene.getStylesheets().add(css);

    stage.setTitle("Image Analysis");
    stage.setScene(scene);
    stage.sizeToScene();  // Auto-size the stage

    // Optional: Set minimum window size to prevent too small resizing
    stage.setMinWidth(800);  // Adjust these values as needed
    stage.setMinHeight(400); // Adjust these values as needed

    stage.show();
}


    public static void main(String[] args) {
        launch();
    }
}