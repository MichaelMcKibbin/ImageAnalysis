package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class OriginalImageProcessor implements ImageProcessor {

    /**
     * Processes an image applying the specified parameters for brightness, color, saturation, and hue.
     *
     * @param originalImage The source image to be processed
     * @param params Processing parameters containing adjustment values
     * @return A new Image instance with the applied adjustments
     *
     * @implNote Brightness uses exponential scaling (2^adjustment) where:
     *          - 0.0 represents original brightness (2^0 = 1.0)
     *          - Negative values darken the image
     *          - Positive values brighten the image
     */
    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Create a new writable image to store the processed result
        WritableImage processedImage = new WritableImage(width, height);
        PixelReader reader = originalImage.getPixelReader();
        PixelWriter writer = processedImage.getPixelWriter();

        // Process each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                // Initialize RGB values with original colors (range 0.0 to 1.0)
                double red = color.getRed();
                double green = color.getGreen();
                double blue = color.getBlue();

                // Apply brightness adjustment using exponential scaling
                // 2^0 = 1.0 (no change), 2^1 = 2.0 (twice as bright), 2^-1 = 0.5 (half as bright)
                double brightnessAdjustment = Math.pow(2.0, params.getBrightness());
                red *= brightnessAdjustment;
                green *= brightnessAdjustment;
                blue *= brightnessAdjustment;




                // Apply red, green, and blue adjustments
//                if (params.getRed() != 0.0) red *= (1.0 + params.getRed());
                // Try this alternative approach for red adjustment
                if (params.getRed() != 0.0) {
                    red = color.getRed() * (1.0 + params.getRed());
                }


                // Create final color, ensuring values are within valid range
                Color newColor = Color.color(
                        clamp(red),
                        clamp(green),
                        clamp(blue),
                        color.getOpacity() // Preserve original opacity
                );

                writer.setColor(x, y, newColor);
            }
        }

        return processedImage;
    }

    /**
     * Ensures color values remain within the valid range of 0.0 to 1.0
     *
     * @param value The color value to be clamped
     * @return A value between 0.0 and 1.0 inclusive
     */
    private double clamp(double value) {
        return Math.min(1.0, Math.max(0.0, value));
    }

    /**
     * Processes an image using default parameters (no adjustments).
     *
     * @param originalImage The source image to be processed
     * @return A copy of the original image with no adjustments
     */
    @Override
    public Image processImage(Image originalImage) {
        ProcessingParameters defaultParams = new ProcessingParameters(
                0.0,     // brightness
                0.0,     // saturation
                0.0,     // hue
                0.0,     // red
                0.0,     // green
                0.0,     // blue
                50.0,    // redCellThreshold (not used in this processor)
                50.0,    // whiteCellThreshold (not used in this processor)
                50.0     // minCellSize (not used in this processor)
        );
        System.out.println("Default red parameter: " + defaultParams.getRed());
        return processImage(originalImage, defaultParams);
    }

    /**
     * @return The display name of this image processor (This is used in the choicebox)
     */
    @Override
    public String getProcessorName() {
        return "Copy of Original";
    }
}