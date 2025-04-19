
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
        processorComboBox.getItems().clear();
        processorComboBox.setPromptText("Choose process");

        // Initialize processors that need callbacks
        UnionFindBlood2 unionFindProcessor = new UnionFindBlood2();
        unionFindProcessor.setResultCallback(processedImage -> {
            imageViewProcessed.setImage(processedImage);
        });

        tricolourProcessor = new TricolourBloodProcessor();
        tricolourProcessor.setImageDisplayCallback(image -> {
            imageViewProcessed.setImage(image);
        });

        // Initialize and add all processors
        imageProcessors = Arrays.asList(
            new OriginalImageProcessor(),
            new BlackAndWhiteProcessor(),
            new BloodCellProcessor(),
            //new TricolourBloodProcessor(),
            tricolourProcessor,
            unionFindProcessor,
            new UnionFindBloodCellProcessor()
            //new UnionFindBlood2()
        );

        processorComboBox.getItems().addAll(imageProcessors);

        // Set up the combo box converter
        processorComboBox.setConverter(new StringConverter<ImageProcessor>() {
            @Override
            public String toString(ImageProcessor processor) {
                return processor != null ? processor.getProcessorName() : "";
            }
            @Override
            public ImageProcessor fromString(String string) {
                return null;
            }
        });

        // Add listener for processor changes
        processorComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    resetSlidersToDefault();
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
        sliderRed.setMin(-100);
        sliderRed.setMax(100);
        sliderRed.setValue(0);

        sliderGreen.setMin(-100);
        sliderGreen.setMax(100);
        sliderGreen.setValue(0);

        sliderBlue.setMin(-100);
        sliderBlue.setMax(100);
        sliderBlue.setValue(0);

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

    private void setupSliderListeners() {
        // Create a list of all sliders
        List<Slider> sliders = Arrays.asList(
            sliderBrightness, sliderHue, sliderSaturation,
            sliderRed, sliderGreen, sliderBlue,
            sliderWhiteCellSensitivity, sliderRedCellSensitivity, sliderCellSizeThreshold
        );

        // Add listener to each slider with debouncing
        sliders.forEach(slider -> {
            slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (Math.abs(newVal.doubleValue() - oldVal.doubleValue()) > 0.01) {
                    updateImage();
                }
            });
        });
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

            // Handle async processors differently
            if (selectedProcessor instanceof UnionFindBlood2 ||
                selectedProcessor instanceof TricolourBloodProcessor) {
                // These processors will update the image view through their callbacks
                selectedProcessor.processImage(imageViewOriginal.getImage(), params);
            } else {
                // Synchronous processors
                Image processedImage = selectedProcessor.processImage(imageViewOriginal.getImage(), params);
                imageViewProcessed.setImage(processedImage);
            }
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

            // Handle async processors differently
            if (selectedProcessor instanceof UnionFindBlood2 ||
                selectedProcessor instanceof TricolourBloodProcessor) {
                // These processors will update the image view through their callbacks
                selectedProcessor.processImage(imageViewOriginal.getImage(), params);
            } else {
                // Synchronous processors
                Image processedImage = selectedProcessor.processImage(imageViewOriginal.getImage(), params);
                imageViewProcessed.setImage(processedImage);
            }
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

            // Reset all sliders to disabled state first
            sliderHue.setDisable(true);
            sliderSaturation.setDisable(true);
            sliderWhiteCellSensitivity.setDisable(true);
            sliderRedCellSensitivity.setDisable(true);
            sliderCellSizeThreshold.setDisable(true);

            if (currentProcessor instanceof BlackAndWhiteProcessor) {
                sliderBrightness.setValue(25);
                sliderRed.setValue(50);
                sliderGreen.setValue(50);
                sliderBlue.setValue(50);
            }
            else if (currentProcessor instanceof BloodCellProcessor) {
                defaults = ProcessingParameters.getDefaultBloodCellDetection();
                sliderHue.setDisable(false);
                sliderSaturation.setDisable(false);
            }
            else if (currentProcessor instanceof UnionFindBlood2) {
                // Enable blood cell detection sliders
                sliderWhiteCellSensitivity.setDisable(false);
                sliderRedCellSensitivity.setDisable(false);
                sliderCellSizeThreshold.setDisable(false);

                // Set default values
                sliderWhiteCellSensitivity.setValue(40);
                sliderRedCellSensitivity.setValue(60);
                sliderCellSizeThreshold.setValue(50);
                sliderBrightness.setValue(0);
                sliderRed.setValue(0);
                sliderGreen.setValue(0);
                sliderBlue.setValue(0);
            }
            else {
                sliderBrightness.setValue(0);
                sliderRed.setValue(0);
                sliderGreen.setValue(0);
                sliderBlue.setValue(0);
            }
            updateImage();
        }
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
            if (processor instanceof UnionFindBlood2 ||
                processor instanceof TricolourBloodProcessor) {
                // Async processors handle their own image updates
                processor.processImage(imageViewOriginal.getImage(), params);
            } else {
                // Synchronous processors
                Image processedImage = processor.processImage(imageViewOriginal.getImage(), params);
                imageViewProcessed.setImage(processedImage);
            }
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

    @FXML
    public void onDefaultSettingsButtonClick(ActionEvent actionEvent) {
        ImageProcessor currentProcessor = processorComboBox.getValue();
        if (currentProcessor != null) {
            resetSlidersToDefault();
        } else {
            // Default values if no processor is selected
            sliderBrightness.setValue(0);
            sliderRed.setValue(0);
            sliderGreen.setValue(0);
            sliderBlue.setValue(0);
            updateImage();
        }
    }
}


