package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


// Method to process the image with tricolour blood effect
// Assumptions: Original image uses Romanowsky staining. The Nucleus of White Blood Cells are typically a darker purple than Red Blood Cells which are typically a pale pink.
// After processing, the White blood cell nucleus should be coloured purple. The red blood cells should be coloured red and the background should be white.


public class TricolourBloodProcessor implements ImageProcessor {
    @Override
    public String getProcessorName() {
        return "Tricolour Blood Analysis";
    }

    @Override
    public Image processImage(Image originalImage) {
        ProcessingParameters defaultParams = new ProcessingParameters(
                0.0,    // brightness
                0.0,     // saturation
                0.0,     // hue
                0.0,    // red
                0.0,     // green
                0.0,    // blue
                50.0,    // redCellThreshold
                50.0,    // whiteCellThreshold
                50.0     // minCellSize - set to middle of range (0-100)
        );

        return processImage(originalImage, defaultParams);
    }

    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage processedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = processedImage.getPixelWriter();

        // Apply brightness and color adjustments from parameters
        double brightness = params.getBrightness();
        double red = params.getRed();
        double green = params.getGreen();
        double blue = params.getBlue();

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Adjust color values based on parameters
                double adjustedRed = color.getRed() * red;
                double adjustedBlue = color.getBlue() * blue;
                double adjustedGreen = color.getGreen() * green;

                // Calculate purple component (combination of red and blue)
                double purpleIntensity = (adjustedRed + adjustedBlue) / 2 - adjustedGreen;

                // Apply brightness adjustment
                purpleIntensity += brightness;

                // Threshold for identifying blood cells
                if (purpleIntensity > 0.2) {  // Adjust threshold as needed
                    // Check if it's a WBC (darker purple in original)
                    if (color.getBrightness() < 0.5) {
                        // Deep purple for WBCs
                        pixelWriter.setColor(x, y, Color.rgb(75, 0, 130));
                    } else {
                        // Pink/light purple for RBCs
                        pixelWriter.setColor(x, y, Color.rgb(219, 112, 147));
                    }
                } else {
                    // Background or plasma
                    pixelWriter.setColor(x, y, Color.WHITE);
                }
            }
        }

        return processedImage;
    }


}
