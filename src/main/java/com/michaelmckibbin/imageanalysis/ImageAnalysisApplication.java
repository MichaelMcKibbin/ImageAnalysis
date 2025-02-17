package com.michaelmckibbin.imageanalysis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class ImageAnalysisApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ImageAnalysisApplication.class.getResource("imageAnalysis-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    
        // Add CSS to the scene
        String css = ImageAnalysisApplication.class.getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Image Analysis");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}