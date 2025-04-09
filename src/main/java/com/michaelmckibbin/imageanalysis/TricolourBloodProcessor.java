package com.michaelmckibbin.imageanalysis;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import com.michaelmckibbin.imageanalysis.UnionFind;

// Method to process the image with tricolour blood effect
// Assumptions: Original image uses Romanowsky staining. The Nucleus of White Blood Cells are typically a darker purple than Red Blood Cells which are typically a pale pink.
// After processing, the White blood cell nucleus should be coloured purple. The red blood cells should be coloured red and the background should be white.

public class TricolourBloodProcessor implements ImageProcessor {
    @Override
    public String getProcessorName() {
        return "Tricolour Blood Analysis *";
    }

    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Create two images: one for initial detection and one for final output
        WritableImage initialDetection = new WritableImage(width, height);
        WritableImage processedImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter initialWriter = initialDetection.getPixelWriter();

        // Apply brightness and color adjustments from parameters
        double brightness = params.getBrightness();
        double red = params.getRed() == 0.0 ? 1.0 : params.getRed();
        double green = params.getGreen() == 0.0 ? 1.0 : params.getGreen();
        double blue = params.getBlue() == 0.0 ? 1.0 : params.getBlue();

        // First pass: Initial cell detection
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Your existing detection logic
                double purpleIntensity = (color.getRed() + color.getBlue()) / 2.0 - color.getGreen();
                purpleIntensity += brightness;

                if (purpleIntensity > 0.15) {
                    if (color.getBrightness() < 0.6 &&
                        color.getBlue() > color.getRed() &&
                        color.getBlue() > color.getGreen()) {
                        // Deep purple for WBCs
                        initialWriter.setColor(x, y, Color.rgb(75, 0, 130));
                    } else {
                        // Pink/light purple for RBCs
                        initialWriter.setColor(x, y, Color.rgb(219, 112, 147));
                    }
                } else {
                    initialWriter.setColor(x, y, Color.WHITE);
                }
            }
        }

        //=========

        // Show initial detection result and pause
        Platform.runLater(() -> {
            // Notify the UI to display the initial detection
            if (imageDisplayCallback != null) {
                imageDisplayCallback.accept(initialDetection);
            }

            // Create a pause using Timeline
            Timeline pause = new Timeline(
                    new KeyFrame(Duration.seconds(3), event -> {
                        // After pause, proceed with second pass
                        processSecondPass(initialDetection, processedImage, width, height);

                        // Show final result
                        if (imageDisplayCallback != null) {
                            imageDisplayCallback.accept(processedImage);
                        }
                    })
            );
            pause.play();
        });



        return processedImage; // Return initial detection immediately
    }

    // Add this field to the class
    private Consumer<Image> imageDisplayCallback;

    // Add this method to the class
    public void setImageDisplayCallback(Consumer<Image> callback) {
        this.imageDisplayCallback = callback;
    }

    // Move second pass processing to a separate method
    private void processSecondPass(Image initialDetection, WritableImage processedImage, int width, int height) {


        //=========



        // Create UnionFind structure
        UnionFind uf = new UnionFind(width * height);

        // Second pass: Connect adjacent cells
        PixelReader initialReader = initialDetection.getPixelReader();

        int purpleCount = 0;
        int redCount = 0;

        // Count initial cells
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = initialReader.getColor(x, y);
                if (isPurple(color)) purpleCount++;
                if (isRed(color)) redCount++;
            }
        }
        System.out.println("Initial counts - Purple: " + purpleCount + ", Red: " + redCount);


        // continue second pass
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color currentColor = initialReader.getColor(x, y);


                if (isCell(currentColor)) {
                    int currentPixel = y * width + x;

                    // Check neighbors (8-connectivity)
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int newX = x + dx;
                            int newY = y + dy;
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                                Color neighborColor = initialReader.getColor(newX, newY);
                                if (isCell(neighborColor)) {
                                    uf.union(currentPixel, newY * width + newX);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create a map to track components containing purple pixels
        Map<Integer, Boolean> hasPurple = new HashMap<>();

        // Third pass: Identify components containing purple pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = initialReader.getColor(x, y);
                if (isPurple(color)) {
                    int root = uf.find(y * width + x);
                    hasPurple.put(root, true);
                }
            }
        }

        // Final pass: Write output image
        PixelWriter finalWriter = processedImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = initialReader.getColor(x, y);
                if (isCell(color)) {
                    int root = uf.find(y * width + x);
                    if (isPurple(color)) {
                        // Keep purple pixels
                        finalWriter.setColor(x, y, color);
                    } else if (!hasPurple.getOrDefault(root, false)) {
                        // Keep red pixels only if not connected to purple
                        finalWriter.setColor(x, y, color);
                    } else {
                        // Remove red pixels connected to purple
                        finalWriter.setColor(x, y, Color.WHITE);
                    }
                } else {
                    finalWriter.setColor(x, y, Color.WHITE);
                }
            }
        }

        // After final pass, count remaining cells
        purpleCount = 0;
        redCount = 0;
        PixelReader finalReader = processedImage.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = finalReader.getColor(x, y);
                if (isPurple(color)) purpleCount++;
                if (isRed(color)) redCount++;
            }
        }
        System.out.println("Final counts - Purple: " + purpleCount + ", Red: " + redCount);
    }



    @Override
    public Image processImage(Image originalImage) {
        return null;
    }

    // Add these helper methods at the end of your class
    private boolean isCell(Color color) {
        return isPurple(color) || isRed(color);
    }

    private boolean isPurple(Color color) {
        double tolerance = 0.01;  // Adjust this value if needed
        return Math.abs(color.getRed() - 75.0/255.0) < tolerance &&
                Math.abs(color.getGreen() - 0.0) < tolerance &&
                Math.abs(color.getBlue() - 130.0/255.0) < tolerance;
    }

    private boolean isRed(Color color) {
        double tolerance = 0.01;  // Adjust this value if needed
        return Math.abs(color.getRed() - 219.0/255.0) < tolerance &&
                Math.abs(color.getGreen() - 112.0/255.0) < tolerance &&
                Math.abs(color.getBlue() - 147.0/255.0) < tolerance;
    }
}

