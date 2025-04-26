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
        System.out.println("\n*****************************************************");
        System.out.println("\n*   Processing image with BlackAndWhiteProcessor    *");
        System.out.println("\n*****************************************************");
        System.out.println("\n");
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage processedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = processedImage.getPixelWriter();

        // Adjust threshold based on brightness parameter (inverted effect)
        double threshold = DEFAULT_THRESHOLD * (1.0 - (params.getBrightness() * 0.5));
        threshold = Math.min(1.0, Math.max(0.0, threshold));

        // Pre-calculate RGB adjustment by averaging the RGB parameters
// This allows for custom weighting of color channels before the black/white conversion
// Values > 1.0 will make that color contribute more to the final brightness
// Values < 1.0 will reduce that color's contribution to the final brightness
double rgbAdjustment = (params.getRed() + params.getGreen() + params.getBlue()) / 3.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Calculate luminance using weighted RGB values based on human perception
                // The coefficients (0.299, 0.587, 0.114) are derived from human visual perception:
                // - Green (0.587) has the highest weight because human eyes are most sensitive to green light
                // - Red (0.299) has the second highest weight due to moderate sensitivity
                // - Blue (0.114) has the lowest weight as human eyes are least sensitive to blue
                // These weights ensure the grayscale conversion matches human perception of brightness
                // An alternate set of values that could be applied are: R:0.2126, G:0.7152, B:0.0722.
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
    public String getProcessorName() {
        return "Black & White";
    }
}

