package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class OriginalImageProcessor implements ImageProcessor {
    private static final double DEFAULT_BRIGHTNESS = -10.0;  // Set to match the working level

    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage processedImage = new WritableImage(width, height);
        PixelReader reader = originalImage.getPixelReader();
        PixelWriter writer = processedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                // Use your working brightness formula
                double brightnessScale = Math.pow(5.0, params.getBrightness()); // This is very annoying!
                double red = color.getRed() * brightnessScale;
                double green = color.getGreen() * brightnessScale;
                double blue = color.getBlue() * brightnessScale;

                // Apply RGB adjustments
                red *= (1.0 + params.getRed());
                green *= (1.0 + params.getGreen());
                blue *= (1.0 + params.getBlue());

                // Apply saturation
                if (params.getSaturation() != 0) {
                    double avg = (red + green + blue) / 3.0;
                    double saturationScale = 1.0 + (params.getSaturation() / 50.0);
                    red = avg + (red - avg) * saturationScale;
                    green = avg + (green - avg) * saturationScale;
                    blue = avg + (blue - avg) * saturationScale;
                }

                // Apply hue adjustment
                if (params.getHue() != 0) {
                    Color adjusted = Color.color(
                        clamp(red),
                        clamp(green),
                        clamp(blue)
                    ).deriveColor(
                        params.getHue() * 360,
                        1.0,
                        1.0,
                        1.0
                    );
                    red = adjusted.getRed();
                    green = adjusted.getGreen();
                    blue = adjusted.getBlue();
                }

                // Create final color with clamped values
                Color newColor = Color.color(
                    clamp(red),
                    clamp(green),
                    clamp(blue),
                    color.getOpacity()
                );

                writer.setColor(x, y, newColor);
            }
        }

        return processedImage;
    }

    private double clamp(double value) {
        return Math.min(1.0, Math.max(0.0, value));
    }

    @Override
    public Image processImage(Image originalImage) {
        ProcessingParameters defaultParams = new ProcessingParameters(
                DEFAULT_BRIGHTNESS,  // brightness set to -10.0 to match original
                0.0,     // saturation
                0.0,     // hue
                0.0,     // red
                0.0,     // green
                0.0,     // blue
                50.0,    // redCellThreshold (unused)
                50.0,    // whiteCellThreshold (unused)
                50.0     // minCellSize (unused)
        );
        return processImage(originalImage, defaultParams);
    }

    @Override
    public String getProcessorName() {
        return "Original Image";
    }
}
