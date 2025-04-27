
package com.michaelmckibbin.imageanalysis;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.util.StringConverter;

import javax.imageio.ImageIO;



/**
 * Controller class for the Image Analysis application.
 * Manages the user interface and processing operations for blood cell image analysis.
 * Supports multiple image processing modes, adjustments, and cell detection parameters.
 */
public class ImageAnalysisController {
    @FXML public HBox slidersHbox1;
    @FXML public HBox slidersHbox2;
    @FXML public HBox slidersHbox3;
    @FXML public MenuItem loadImage;
    @FXML public MenuItem setDefaultImagesDir;
    @FXML public HBox imageChoicesBox;
    @FXML public MenuItem saveImageAs;
    @FXML private ImageView imageViewOriginal;
    @FXML private ImageView imageViewProcessed;
    @FXML private ComboBox<ImageProcessor> processorComboBox;

    // Sliders
    @FXML private Slider sliderBrightness;
    @FXML private Slider sliderHue;
    @FXML private Slider sliderMinCellSize;
    @FXML private Slider sliderRed;
    @FXML private Slider sliderGreen;
    @FXML private Slider sliderBlue;
    @FXML public Slider sliderRedCellSensitivity;
    @FXML public Slider sliderWhiteCellSensitivity;
    @FXML public Slider sliderCellSizeThreshold;

    /**
     * List of available image processors that can be applied to the image.
     */
    private List<ImageProcessor> imageProcessors;

    /**
     * Stores the directory where the application is running.
     * Used for setting default save locations.
     */
    private File defaultImageDirectory;

    private TricolourBloodProcessor tricolourProcessor;

    /**
     * Stores reference to the currently loaded image file.
     * Used for determining default save names and formats.
     */
    private File currentImageFile;

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
        ConnectedComponentsProcessor unionFindProcessor = new ConnectedComponentsProcessor();
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
            //new BloodCellProcessor(),
            //new TricolourBloodProcessor(),
            tricolourProcessor,
            unionFindProcessor,
            new UnionFindBloodCellProcessor()
            //new ConnectedComponentsProcessor()
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
            sliderBrightness, sliderHue, sliderMinCellSize,
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

    /**
     * Updates the image view with a new image.
     * Adjusts the view to fit the window while maintaining aspect ratio.
     */
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

            /**
             * Creates processing parameters based on current slider values.
             * Combines all adjustment parameters into a single ProcessingParameters object.
             *
             * @return ProcessingParameters object containing current adjustment values
             */
            ProcessingParameters params = createProcessingParameters();

            // Handle async processors differently
            if (selectedProcessor instanceof ConnectedComponentsProcessor ||
                selectedProcessor instanceof TricolourBloodProcessor) {
                // These processors update the image view through their callbacks
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
            if (selectedProcessor instanceof ConnectedComponentsProcessor ||
                selectedProcessor instanceof TricolourBloodProcessor) {
                // These processors update the image view through their callbacks
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
            sliderMinCellSize.setDisable(true);
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
                sliderMinCellSize.setDisable(false);
            }
            else if (currentProcessor instanceof ConnectedComponentsProcessor) {
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

    /**
     * Processes the current image using the selected processor and parameters.
     * Updates the UI with the processing result.
     *
     * @param processor The image processor to use
     */
    private void processImage(ImageProcessor processor) {
        if (imageViewOriginal.getImage() != null) {
            ProcessingParameters params = createProcessingParameters();
            if (processor instanceof ConnectedComponentsProcessor ||
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


 /**
 * Handles the opening of image files.
 * Displays a file chooser dialog with "All Images" as the default filter option.
 *
 * @param event The action event triggered by the open file button
 */
@FXML
private void handleOpenFile(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Image File");
    fileChooser.setInitialDirectory(defaultImageDirectory);

    // Create extension filters
    FileChooser.ExtensionFilter allImagesFilter =
        new FileChooser.ExtensionFilter("All Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
    FileChooser.ExtensionFilter pngFilter =
        new FileChooser.ExtensionFilter("PNG Files", "*.png");
    FileChooser.ExtensionFilter jpegFilter =
        new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg");
    FileChooser.ExtensionFilter gifFilter =
        new FileChooser.ExtensionFilter("GIF Files", "*.gif");
    FileChooser.ExtensionFilter bmpFilter =
        new FileChooser.ExtensionFilter("BMP Files", "*.bmp");
    FileChooser.ExtensionFilter tiffFilter =
        new FileChooser.ExtensionFilter("TIFF Files", "*.tiff", "*.tif");
    FileChooser.ExtensionFilter webpFilter =
        new FileChooser.ExtensionFilter("WEBP Files", "*.webp");


    // Add filters in desired order, with "All Images" first
    fileChooser.getExtensionFilters().addAll(
        allImagesFilter,
        pngFilter,
        jpegFilter,
        gifFilter,
        bmpFilter,
        tiffFilter,
        webpFilter
    );

    // Set "All Images" as the default filter
    fileChooser.setSelectedExtensionFilter(allImagesFilter);

    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null) {
        try {
            currentImageFile = selectedFile;
            String imageUrl = selectedFile.toURI().toURL().toExternalForm();
            imageViewOriginal.setImage(new Image(imageUrl));
            updateImage();
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

/**
 * Saves the processed image with a filename based on the original name plus timestamp.
 * Maintains the original file format while allowing user to choose a different format.
 *
 * @param event The action event triggered by the save button
 */
@FXML
private void handleSaveImageAs(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Image File");
    fileChooser.setInitialDirectory(defaultImageDirectory);


    /**
     * Configuration for the file chooser dialog.
     * Add support for multiple common image formats.
     */
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.tif", "*.tiff", "*.webp"),
            new FileChooser.ExtensionFilter("PNG Files", "*.png"),
            new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("GIF Files", "*.gif"),
            new FileChooser.ExtensionFilter("BMP Files", "*.bmp"),
            new FileChooser.ExtensionFilter("TIFF Files", "*.tiff", "*.tif"),
            new FileChooser.ExtensionFilter("WEBP Files", "*.webp")
            // Add more formats as needed
    );

    // Generate timestamp for filename
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    String timestamp = LocalDateTime.now().format(formatter);

    // Set default filename and format based on original image
    if (currentImageFile != null) {
        String originalFileName = currentImageFile.getName();
        String originalExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        String baseFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String defaultFileName = String.format("%s_processed_%s.%s", baseFileName, timestamp, originalExtension);
        fileChooser.setInitialFileName(defaultFileName);

        // Set the extension filter to match the original image format
        for (FileChooser.ExtensionFilter filter : fileChooser.getExtensionFilters()) {
            if (filter.getExtensions().contains("*." + originalExtension)) {
                fileChooser.setSelectedExtensionFilter(filter);
                break;
            }
        }
    } else {
        fileChooser.setInitialFileName("processed_image_" + timestamp + ".png");
        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0)); // PNG default
    }

    File selectedFile = fileChooser.showSaveDialog(null);
    if (selectedFile != null) {
        try {
            Image imageToSave = imageViewProcessed.getImage();
            BufferedImage bImage = SwingFXUtils.fromFXImage(imageToSave, null);

            // Determine format from the selected file extension
            String format = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.') + 1).toLowerCase();

            // Handle JPEG format specifically to avoid alpha channel issues
            if (format.equals("jpg") || format.equals("jpeg")) {
                // Convert to RGB if image has alpha channel
                BufferedImage rgbImage = new BufferedImage(bImage.getWidth(), bImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = rgbImage.createGraphics();
                graphics.drawImage(bImage, 0, 0, null);
                graphics.dispose();
                bImage = rgbImage;
            }

            ImageIO.write(bImage, format, selectedFile);
        } catch (IOException e) {
            showErrorAlert("Save Error", "Could not save the image.");
            e.printStackTrace();
        }
    } else {
        showErrorAlert("Save Error", "No file selected.");
    }
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


