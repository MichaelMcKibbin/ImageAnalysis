package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;

public class OriginalImageProcessor implements ImageProcessor {
    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        return originalImage; // Simply return the original image without modification
    }
    @Override
    public Image processImage(Image originalImage) {
        // Use default parameters when no parameters are provided
        ProcessingParameters defaultParams = new ProcessingParameters(
                0.0,     // brightness
                0.0,     // saturation
                0.0,     // hue
                0.0,     // red
                0.0,     // green
                0.0,     // blue
                50.0,    // redCellThreshold
                50.0,    // whiteCellThreshold
                50.0     // minCellSize
        );
        return processImage(originalImage, defaultParams);
    }
    @Override
    public String getProcessorName() {
        return "Copy of Original Image";
    }
}