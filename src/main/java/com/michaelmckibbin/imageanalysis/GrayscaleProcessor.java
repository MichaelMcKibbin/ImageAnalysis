package com.michaelmckibbin.imageanalysis;

import com.michaelmckibbin.imageanalysis.ImageProcessor;
import com.michaelmckibbin.imageanalysis.ProcessingParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

//public class GrayscaleProcessor implements ImageProcessor {
//    @Override
//    public Image processImage(Image originalImage, ProcessingParameters params) {
//        // Debug output
//        System.out.println("Processing image with dimensions: " +
//            originalImage.getWidth() + "x" + originalImage.getHeight());
//        System.out.println("Parameters: brightness=" + params.getBrightness() +
//            " red=" + params.getRed() +
//            " green=" + params.getGreen() +
//            " blue=" + params.getBlue());
//
//        int width = (int) originalImage.getWidth();
//        int height = (int) originalImage.getHeight();
//
//        WritableImage processedImage = new WritableImage(width, height);
//        PixelReader pixelReader = originalImage.getPixelReader();
//        PixelWriter pixelWriter = processedImage.getPixelWriter();
//
//        // Process just the first pixel to debug
//        Color firstPixel = pixelReader.getColor(0, 0);
//        System.out.println("First pixel RGB: " +
//            firstPixel.getRed() + ", " +
//            firstPixel.getGreen() + ", " +
//            firstPixel.getBlue());
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//
//                // Calculate grayscale value using weighted RGB
//                double gray = (0.299 * color.getRed() +
//                             0.587 * color.getGreen() +
//                             0.114 * color.getBlue());
//
//                // Apply brightness
//                double brightnessAdjustment = 1.0 + params.getBrightness();
//                gray *= brightnessAdjustment;
//
//                // Apply RGB adjustments
//                double rgbAdjustment = (params.getRed() + params.getGreen() + params.getBlue()) / 3.0;
//                gray *= rgbAdjustment;
//
//                // Ensure value stays in valid range
//                gray = Math.min(1.0, Math.max(0.0, gray));
//
//                // Debug output for first pixel
//                if (x == 0 && y == 0) {
//                    System.out.println("First pixel after processing: " + gray);
//                }
//
//                Color grayColor = new Color(gray, gray, gray, color.getOpacity());
//                pixelWriter.setColor(x, y, grayColor);
//            }
//        }
//        return processedImage;
//    }
public class GrayscaleProcessor implements ImageProcessor {
    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage processedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = processedImage.getPixelWriter();

        // Pre-calculate adjustments
        double brightnessAdjustment = 1.0 + params.getBrightness();
        double rgbAdjustment = (params.getRed() + params.getGreen() + params.getBlue()) / 3.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Calculate grayscale using perceptual weights
                double gray = (0.299 * color.getRed() +
                             0.587 * color.getGreen() +
                             0.114 * color.getBlue());

                // Apply adjustments
                gray = gray * brightnessAdjustment * rgbAdjustment;

                // Ensure value stays in valid range
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
