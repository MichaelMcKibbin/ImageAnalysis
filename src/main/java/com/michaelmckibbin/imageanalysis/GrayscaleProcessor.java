package com.michaelmckibbin.imageanalysis;

import com.michaelmckibbin.imageanalysis.ImageProcessor;
import com.michaelmckibbin.imageanalysis.ProcessingParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


/**
 * Processes images by converting them to grayscale while allowing for brightness and RGB adjustments.
 * This processor implements the ImageProcessor interface and uses the ITU-R BT.601 conversion formula
 * for calculating luminance (grayscale) values from RGB components.
 */

public class GrayscaleProcessor implements ImageProcessor {

    /**
     * Converts a color image to grayscale with adjustable parameters for brightness and RGB balance.
     *
     * @param originalImage The source image to be processed
     * @param params Processing parameters containing brightness and RGB adjustment values
     * @return A new Image instance containing the processed grayscale version
     *
     * @implNote The grayscale conversion uses the following weights for RGB components:
     *          Red: 0.299
     *          Green: 0.587
     *          Blue: 0.114
     *          These weights are based on human perception of color (ITU-R BT.601 standard).
     */
    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage processedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = processedImage.getPixelWriter();

        // Pre-calculate adjustments for performance optimization
        double brightnessAdjustment = 1.0 + params.getBrightness();
        double rgbAdjustment = (params.getRed() + params.getGreen() + params.getBlue()) / 3.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Calculate grayscale using perceptual weights (ITU-R BT.601)
                double gray = (0.299 * color.getRed() +
                             0.587 * color.getGreen() +
                             0.114 * color.getBlue());

                // Apply brightness and RGB balance adjustments
                gray = gray * brightnessAdjustment * rgbAdjustment;

                // Clamp values to valid range [0.0, 1.0]
                gray = Math.min(1.0, Math.max(0.0, gray));

                Color grayColor = new Color(gray, gray, gray, color.getOpacity());
                pixelWriter.setColor(x, y, grayColor);
            }
        }
        return processedImage;
    }

    @Override
    public Image processImage(Image originalImage) {
        return processImage(originalImage, ProcessingParameters.getDefaultGrayscale());
    }

    @Override
    public String getProcessorName() {
        return "Grayscale";
    }
}
