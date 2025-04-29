package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Processes images to convert them to black and white using configurable thresholds
 * and RGB channel weightings. This processor implements advanced image processing
 * techniques considering human visual perception for optimal black and white conversion.
 *
 *
 * @author Michael McKibbin (20092733)
 * @version 1.0 (2024-02-20)
 *
 */
public class BlackAndWhiteProcessor implements ImageProcessor {
    /**
     * Default threshold value for black/white conversion.
     * Value of 0.35 provides better initial detail compared to previous 0.5 value.
     */
    private static final double DEFAULT_THRESHOLD = 0.35; // previous value 0.5.

    /**
     * Processes an image to convert it to black and white using specified parameters.
     * The conversion takes into account:
     * <ul>
     *   <li>Brightness adjustments with inverse threshold effects</li>
     *   <li>Custom RGB channel weighting</li>
     *   <li>Human perception-based luminance calculations</li>
     * </ul>
     *
     * @param originalImage The source image to be processed
     * @param params Processing parameters including brightness and RGB channel weights
     * @return A new Image instance containing the black and white version
     */
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
        // Higher brightness values results in a lower threshold, making more pixels turn white
        //Lower brightness values results in a higher threshold, making more pixels turn black
        //
        double threshold = DEFAULT_THRESHOLD * (1.0 - (params.getBrightness() * 0.5));
        threshold = Math.min(1.0, Math.max(0.0, threshold)); // clamp operation, between 0.0 & 1.0.

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

    /**
     * Processes an image using default black and white conversion parameters.
     * This is a convenience method that uses default processing parameters.
     *
     * @param originalImage The source image to be processed
     * @return A new Image instance containing the black and white version
     */
    @Override
    public Image processImage(Image originalImage) {
        return processImage(originalImage, ProcessingParameters.getDefaultBlackAndWhite());
    }

    /**
     * Returns the name of this image processor.
     *
     * @return The string "Black & White"
     */
    @Override
    public String getProcessorName() {
        return "Black & White";
    }
}

