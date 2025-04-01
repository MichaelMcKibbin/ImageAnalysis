package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class BlackAndWhiteProcessor implements ImageProcessor {
    private static final double DEFAULT_THRESHOLD = 0.35; // Lowered from 0.5 for better initial detail

    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage processedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = processedImage.getPixelWriter();

        // Adjust threshold based on brightness parameter (inverted effect)
        double threshold = DEFAULT_THRESHOLD * (1.0 - (params.getBrightness() * 0.5));
        threshold = Math.min(1.0, Math.max(0.0, threshold));

        // Pre-calculate RGB adjustment
        double rgbAdjustment = (params.getRed() + params.getGreen() + params.getBlue()) / 3.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Calculate luminance using weighted RGB values
                double luminance = (0.299 * color.getRed() +
                                  0.587 * color.getGreen() +
                                  0.114 * color.getBlue());

                // Apply RGB adjustment
                luminance *= rgbAdjustment;

                // Convert to black or white based on threshold
                Color newColor = (luminance > threshold) ? Color.WHITE : Color.BLACK;
                pixelWriter.setColor(x, y, newColor);
            }
        }
        return processedImage;
    }

    @Override
    public Image processImage(Image originalImage) {
        return processImage(originalImage, ProcessingParameters.getDefaultBlackAndWhite());
    }

    @Override
public Image processSecondaryImage(Image originalImage) {
    // You might want to show edges or another useful visualization
    return processImage(originalImage); // Simple default implementation
}


    @Override
    public String getProcessorName() {
        return "Black & White";
    }
}

