package com.michaelmckibbin.imageanalysis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.MalformedURLException;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;


public class ImageAnalysisController {

    public MenuItem loadImage;
    public ImageView originalImage;
    public ImageView adjustedImage;
    public HBox radioButtonBox;
    public Button showOriginalImage;
    public Button ShowBlackAndWhiteImage;
    public HBox slidersHbox1;
    public HBox slidersHbox2;
    public ImageView imageView2;
    public ImageView imageView1;
    @FXML
    private Slider sliderBrightness;
    @FXML
    private Slider sliderHue;
    @FXML
    private Slider sliderSaturation;
    @FXML
    private RadioButton radioRed;
    @FXML
    private RadioButton radioGreen;
    @FXML
    private RadioButton radioBlue;
    @FXML
    private RadioButton radioDefault;
    @FXML
    private Slider sliderRed;
    @FXML
    private Slider sliderGreen;
    @FXML
    private Slider sliderBlue;

    @FXML
    public void initialize() {

        // Set default values for slidersHbox2
        sliderSaturation.setValue(75);
        sliderBrightness.setValue(75);
        sliderHue.setValue(75);

        // initial values for slidersHbox1
        final double DEFAULT_VALUE = 50;

        ToggleGroup colorGroup = new ToggleGroup();
        radioRed.setToggleGroup(colorGroup);
        radioGreen.setToggleGroup(colorGroup);
        radioBlue.setToggleGroup(colorGroup);
        radioDefault.setToggleGroup(colorGroup);

        // Set initial selection to Default
        radioDefault.setSelected(true);

        colorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == radioDefault) {
                sliderRed.setValue(DEFAULT_VALUE);
                sliderGreen.setValue(DEFAULT_VALUE);
                sliderBlue.setValue(DEFAULT_VALUE);
            } else if (newValue == radioRed) {
                sliderRed.setValue(sliderRed.getMax());
                sliderGreen.setValue(sliderGreen.getMin());
                sliderBlue.setValue(sliderBlue.getMin());
            } else if (newValue == radioGreen) {
                sliderRed.setValue(sliderRed.getMin());
                sliderGreen.setValue(sliderGreen.getMax());
                sliderBlue.setValue(sliderBlue.getMin());
            } else if (newValue == radioBlue) {
                sliderRed.setValue(sliderRed.getMin());
                sliderGreen.setValue(sliderGreen.getMin());
                sliderBlue.setValue(sliderBlue.getMax());
            }
        });
    }

    public void loadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        // Set initial directory to the images folder
        // need to choose whether to use system pictures folder or project images folder as default...
        //String userHome = System.getProperty("user.home");
        //File imagesDir = new File(userHome + "/Pictures");  // Default to user's Pictures folder
        File imagesDir = new File("src/main/resources/images"); // Default to project images folder

        if (imagesDir.exists()) {
            fileChooser.setInitialDirectory(imagesDir);
        }

        // Set filters for image files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        // Show file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Convert the file path to URL format
                String imageUrl = selectedFile.toURI().toURL().toExternalForm();
                Image image = new Image(imageUrl);

                // Assuming you have an ImageView named 'imageViewer' in your FXML
                // Replace 'imageViewer' with your actual ImageView variable name
                imageView1.setImage(image);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                // Handle the error appropriately, perhaps show an alert to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Image Loading Error");
                alert.setContentText("Could not load the selected image.");
                alert.showAndWait();
            }
        }
    }


    public void showOriginalImage(ActionEvent actionEvent) {
    }

    public void showBlackAndWhiteImage(ActionEvent actionEvent) {
    }
}
