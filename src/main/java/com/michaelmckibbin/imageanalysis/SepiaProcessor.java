package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

//public class SepiaProcessor implements ImageProcessor {
//    @Override
//    public Image processImage(Image originalImage, ProcessingParameters params) {
//        int width = (int) originalImage.getWidth();
//        int height = (int) originalImage.getHeight();
//
//        WritableImage sepiaImage = new WritableImage(width, height);
//        PixelReader pixelReader = originalImage.getPixelReader();
//        PixelWriter pixelWriter = sepiaImage.getPixelWriter();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//
//                // Extract RGB values
//                double r = color.getRed();
//                double g = color.getGreen();
//                double b = color.getBlue();
//
//                // Calculate sepia values
//                double tr = (r * 0.393 + g * 0.769 + b * 0.189);
//                double tg = (r * 0.349 + g * 0.686 + b * 0.168);
//                double tb = (r * 0.272 + g * 0.534 + b * 0.131);
//
//                // Apply brightness
//                double brightnessAdjustment = 1.0 + params.getBrightness();
//                tr *= brightnessAdjustment;
//                tg *= brightnessAdjustment;
//                tb *= brightnessAdjustment;
//
//                // Apply RGB adjustments
//                tr *= params.getRed();
//                tg *= params.getGreen();
//                tb *= params.getBlue();
//
//                // Ensure values stay in valid range
//                tr = Math.min(1.0, Math.max(0.0, tr));
//                tg = Math.min(1.0, Math.max(0.0, tg));
//                tb = Math.min(1.0, Math.max(0.0, tb));
//
//                // Create new color and set pixel
//                Color sepiaColor = new Color(tr, tg, tb, color.getOpacity());
//                pixelWriter.setColor(x, y, sepiaColor);
//            }
//        }
//        return sepiaImage;
//    }
public class SepiaProcessor implements ImageProcessor {
    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage sepiaImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = sepiaImage.getPixelWriter();

        // Pre-calculate adjustments
        double brightnessAdjustment = 1.0 + params.getBrightness();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();

                // Calculate sepia values with adjusted matrix
                double tr = (r * 0.393 + g * 0.769 + b * 0.189) * params.getRed();
                double tg = (r * 0.349 + g * 0.686 + b * 0.168) * params.getGreen();
                double tb = (r * 0.272 + g * 0.534 + b * 0.131) * params.getBlue();

                // Apply brightness
                tr *= brightnessAdjustment;
                tg *= brightnessAdjustment;
                tb *= brightnessAdjustment;

                // Ensure values stay in valid range
                tr = Math.min(1.0, Math.max(0.0, tr));
                tg = Math.min(1.0, Math.max(0.0, tg));
                tb = Math.min(1.0, Math.max(0.0, tb));

                Color sepiaColor = new Color(tr, tg, tb, color.getOpacity());
                pixelWriter.setColor(x, y, sepiaColor);
            }
        }
        return sepiaImage;
    }


    private int adjustWithParameters(int value, ProcessingParameters params) {
        // Apply brightness
        value = (int) (value * (1 + params.getBrightness()));

        // Apply RGB adjustments
        value = (int) (value * params.getRed());

        return value;
    }

    @Override
    public Image processImage(Image originalImage) {
        return processImage(originalImage, ProcessingParameters.getDefaultSepia());
    }

    @Override
    public Image processSecondaryImage(Image originalImage) {
        // You might want to show original colors or another useful visualization
        return processImage(originalImage); // Simple default implementation
    }

    @Override
    public String getProcessorName() {
        return "Sepia";
    }
}


