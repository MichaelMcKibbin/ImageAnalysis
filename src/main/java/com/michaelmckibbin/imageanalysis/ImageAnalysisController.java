package com.michaelmckibbin.imageanalysis;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.util.StringConverter;


public class ImageAnalysisController {
    @FXML public HBox slidersHbox1;
    @FXML public HBox slidersHbox2;
    @FXML public HBox slidersHbox3;
    @FXML public MenuItem loadImage;
    @FXML public MenuItem setDefaultImagesDir;
    @FXML public HBox imageChoicesBox;
    @FXML private ImageView imageViewOriginal;
    @FXML private ImageView imageViewProcessed;
    @FXML private ComboBox<ImageProcessor> processorComboBox;

    // Sliders
    @FXML private Slider sliderBrightness;
    @FXML private Slider sliderHue;
    @FXML private Slider sliderSaturation;
    @FXML private Slider sliderRed;
    @FXML private Slider sliderGreen;
    @FXML private Slider sliderBlue;
    @FXML public Slider sliderRedCellSensitivity;
    @FXML public Slider sliderWhiteCellSensitivity;
    @FXML public Slider sliderCellSizeThreshold;

    private List<ImageProcessor> imageProcessors;
    private File defaultImageDirectory;

    private TricolourBloodProcessor tricolourProcessor;

    @FXML
    private void initialize() {
        setupProcessors();
        setupSliderDefaults();
        setupSliderListeners();
        initializeDefaultDirectory();
    }
private void setupProcessors() {
    // Clear any existing items first
    processorComboBox.getItems().clear();
    processorComboBox.setPromptText("Choose process");  // Updated prompt text

    // Initialize the TricolourBloodProcessor with callback
    tricolourProcessor = new TricolourBloodProcessor();
    tricolourProcessor.setImageDisplayCallback(image -> {
        imageViewProcessed.setImage(image);
    });


    // Initialize the list if not already done
    imageProcessors = new ArrayList<>();

    // Add processors only once
    imageProcessors.add(new OriginalImageProcessor());
    imageProcessors.add(new BlackAndWhiteProcessor());
    imageProcessors.add(new GrayscaleProcessor());
    imageProcessors.add(new SepiaProcessor());
    imageProcessors.add(new BloodCellProcessor());
    imageProcessors.add(tricolourProcessor);
    imageProcessors.add(new TricolourBloodProcessor());
    imageProcessors.add(new UnionFindBloodCellProcessor());

    // Add other processors as needed

    // Add all processors to combo boxes at once
    processorComboBox.getItems().addAll(imageProcessors);


    // Set up the combo box display
    StringConverter<ImageProcessor> converter = new StringConverter<ImageProcessor>() {
        @Override
        public String toString(ImageProcessor processor) {
            return processor != null ? processor.getProcessorName() : "";
        }
        @Override
        public ImageProcessor fromString(String string) {
            return null; // Not needed for ComboBox
        }
    };

    processorComboBox.setConverter(converter);


    // Remove automatic selection
    // processorComboBox.getSelectionModel().selectFirst();  // Comment out or remove these lines
    // processorComboBox2.getSelectionModel().selectFirst(); // Comment out or remove these lines

    // Clear any default selection
    processorComboBox.getSelectionModel().clearSelection();


    // Add listener to combo boxes for processor changes
    processorComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {  // Add null check
                    if (newVal instanceof BlackAndWhiteProcessor) {
                        // Special defaults for Black & White
                        sliderBrightness.setValue(25);
                        sliderRed.setValue(50);
                        sliderGreen.setValue(50);
                        sliderBlue.setValue(50);
                    } else {
                        // Default values for other processors
                        sliderBrightness.setValue(0);
                        sliderRed.setValue(100);
                        sliderGreen.setValue(100);
                        sliderBlue.setValue(100);
                    }
                    updatePrimaryImage();
                }
            }
    );


}


    private void setupSliderDefaults() {
        // Brightness: -100 to 100 (will be converted to -1.0 to 1.0 in processing)
        sliderBrightness.setMin(-100);
        sliderBrightness.setMax(100);
        sliderBrightness.setValue(0);

        // RGB: 0 to 200 (will be converted to 0.0 to 2.0 in processing)
        sliderRed.setMin(0);
        sliderRed.setMax(200);
        sliderRed.setValue(100);    // 100 = 1.0 multiplier

        sliderGreen.setMin(0);
        sliderGreen.setMax(200);
        sliderGreen.setValue(100);

        sliderBlue.setMin(0);
        sliderBlue.setMax(200);
        sliderBlue.setValue(100);

        sliderWhiteCellSensitivity.setMin(0);
        sliderWhiteCellSensitivity.setMax(100);
        sliderWhiteCellSensitivity.setValue(40);  // default value

        sliderRedCellSensitivity.setMin(0);
        sliderRedCellSensitivity.setMax(100);
        sliderRedCellSensitivity.setValue(60);  // default value

        sliderCellSizeThreshold.setMin(0);
        sliderCellSizeThreshold.setMax(100);
        sliderCellSizeThreshold.setValue(50);  // default value

        // Optional: add labels to show current values
        addValueLabel(sliderBrightness, "Brightness: ");
        addValueLabel(sliderRed, "Red: ");
        addValueLabel(sliderGreen, "Green: ");
        addValueLabel(sliderBlue, "Blue: ");
        addValueLabel(sliderWhiteCellSensitivity, "White Cell Sensitivity: ");
        addValueLabel(sliderRedCellSensitivity, "Red Cell Sensitivity: ");
        addValueLabel(sliderCellSizeThreshold, "Cell Size Threshold: ");

    }

    private void addValueLabel(Slider slider, String prefix) {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
    }


    private void updateImage() {
        ImageProcessor selectedProcessor = processorComboBox.getValue();
        if (selectedProcessor != null && imageViewOriginal.getImage() != null) {
            // Debug output
            System.out.println("Updating image with processor: " + selectedProcessor.getProcessorName());
            System.out.println("Current slider values:");
            System.out.println("Brightness: " + sliderBrightness.getValue());
            System.out.println("Red: " + sliderRed.getValue());
            System.out.println("Green: " + sliderGreen.getValue());
            System.out.println("Blue: " + sliderBlue.getValue());
            System.out.println("White Cell Sensitivity: " + sliderWhiteCellSensitivity.getValue());
            System.out.println("Red Cell Sensitivity: " + sliderRedCellSensitivity.getValue());
            System.out.println("Cell Size Threshold: " + sliderCellSizeThreshold.getValue());

            ProcessingParameters params = createProcessingParameters();
            Image processedImage = selectedProcessor.processImage(imageViewOriginal.getImage(), params);
            imageViewProcessed.setImage(processedImage);
        }

    }

    private void updatePrimaryImage() {
        ImageProcessor selectedProcessor = processorComboBox.getValue();
        if (selectedProcessor != null && imageViewOriginal.getImage() != null) {
            // Debug output
            System.out.println("Updating primary image with processor: " + selectedProcessor.getProcessorName());
            System.out.println("Current slider values:");
            System.out.println("Brightness: " + sliderBrightness.getValue());
            System.out.println("Red: " + sliderRed.getValue());
            System.out.println("Green: " + sliderGreen.getValue());
            System.out.println("Blue: " + sliderBlue.getValue());
            System.out.println("White Cell Sensitivity: " + sliderWhiteCellSensitivity.getValue());
            System.out.println("Red Cell Sensitivity: " + sliderRedCellSensitivity.getValue());
            System.out.println("Cell Size Threshold: " + sliderCellSizeThreshold.getValue());

            ProcessingParameters params = createProcessingParameters();
            Image processedImage = selectedProcessor.processImage(imageViewOriginal.getImage(), params);
            imageViewProcessed.setImage(processedImage);
        }
    }





    private ProcessingParameters createProcessingParameters() {
        double cellSizeValue = sliderCellSizeThreshold.getValue();
        System.out.println("Cell Size Threshold Slider Value: " + cellSizeValue);

        ProcessingParameters params = new ProcessingParameters(
                sliderBrightness.getValue() / 100.0,  // Convert to -1.0 to 1.0
                0.0, // saturation
                0.0, // hue
                sliderRed.getValue() / 100.0,    // Convert to 0.0 to 2.0
                sliderGreen.getValue() / 100.0,
                sliderBlue.getValue() / 100.0,
                sliderRedCellSensitivity.getValue(),
                sliderWhiteCellSensitivity.getValue(),
                cellSizeValue
        );

        System.out.println("Created Parameters - Cell Size: " + params.getMinCellSize());
        return params;
    }




    private void resetSlidersToDefault() {
        ImageProcessor currentProcessor = processorComboBox.getValue();
        if (currentProcessor != null) {
            ProcessingParameters defaults;

            if (currentProcessor instanceof BlackAndWhiteProcessor) {
                sliderBrightness.setValue(25);
                sliderRed.setValue(50);
                sliderGreen.setValue(50);
                sliderBlue.setValue(50);
                // Hide or disable color-related sliders
                sliderHue.setDisable(true);
                sliderSaturation.setDisable(true);
            }
            else if (currentProcessor instanceof GrayscaleProcessor) {
                defaults = ProcessingParameters.getDefaultGrayscale();
                // Hide or disable color-related sliders
                sliderHue.setDisable(true);
                sliderSaturation.setDisable(true);
            }
            else if (currentProcessor instanceof SepiaProcessor) {
                defaults = ProcessingParameters.getDefaultSepia();
                // Enable all sliders for Sepia
                sliderHue.setDisable(false);
                sliderSaturation.setDisable(false);
            }
            else if (currentProcessor instanceof BloodCellProcessor) {
                defaults = ProcessingParameters.getDefaultBloodCellDetection();
                // Enable all sliders for BloodCell Detection
                sliderHue.setDisable(false);
                sliderSaturation.setDisable(false);

            } else {
                sliderBrightness.setValue(0);
                sliderRed.setValue(100);
                sliderGreen.setValue(100);
                sliderBlue.setValue(100);
            }
            updateImage();
        }
    }




private void setupSliderListeners() {
    // Create a list of all sliders
    List<Slider> sliders = Arrays.asList(
        sliderBrightness,
        sliderHue,
        sliderSaturation,
        sliderRed,
        sliderGreen,
        sliderBlue,
        sliderWhiteCellSensitivity,
        sliderRedCellSensitivity,
        sliderCellSizeThreshold
    );

    // Add listener to each slider
    sliders.forEach(slider ->
        slider.valueProperty().addListener((obs, oldVal, newVal) -> updateImage())
    );
}

    private void initializeDefaultDirectory() {
        defaultImageDirectory = new File(System.getProperty("user.dir") +
            "/src/main/resources/com/michaelmckibbin/imageanalysis/images");
        if (!defaultImageDirectory.exists()) {
            defaultImageDirectory = new File(System.getProperty("user.home"));
        }
    }

    private void processImage(ImageProcessor processor) {
        if (imageViewOriginal.getImage() != null) {
            ProcessingParameters params = createProcessingParameters();
            Image processedImage = processor.processImage(imageViewOriginal.getImage(), params);
            imageViewProcessed.setImage(processedImage);
        }
    }

    public void loadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = createConfiguredFileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        loadSelectedFile(selectedFile);
    }

    private FileChooser createConfiguredFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.setInitialDirectory(defaultImageDirectory);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        return fileChooser;
    }

    private void loadSelectedFile(File selectedFile) {
        if (selectedFile != null) {
            try {
                String imageUrl = selectedFile.toURI().toURL().toExternalForm();
                imageViewOriginal.setImage(new Image(imageUrl));
            } catch (MalformedURLException e) {
                showErrorAlert("Image Loading Error", "Could not load the selected image.");
                e.printStackTrace();
            }
        }
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }



    public void setDefaultImagesDirectory(ActionEvent actionEvent) {
    }

    // Add a reset button handler
//    @FXML
//    public void onDefaultSettingsButtonClick(ActionEvent actionEvent) {
//        resetSlidersToDefault();
//        updateImage();
//    }
    @FXML
public void onDefaultSettingsButtonClick(ActionEvent actionEvent) {
    sliderBrightness.setValue(0);
    sliderRed.setValue(100);
    sliderGreen.setValue(100);
    sliderBlue.setValue(100);
    updateImage();
}

}
