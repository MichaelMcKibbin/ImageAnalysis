package com.michaelmckibbin.imageanalysis;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BloodCellProcessor implements ImageProcessor {
    private int originalWidth;
    private int originalHeight;

    public enum ObjectType {
        TYPE_W,  // darker objects
        TYPE_R   // lighter objects
    }

@Override
public Image processImage(Image originalImage, ProcessingParameters params) {
    int width = (int) originalImage.getWidth();
    int height = (int) originalImage.getHeight();

    WritableImage processedImage = new WritableImage(width, height);
    PixelReader pixelReader = originalImage.getPixelReader();
    PixelWriter pixelWriter = processedImage.getPixelWriter();

    double brightness = params.getBrightness();
    double red = params.getRed();
    double green = params.getGreen();
    double blue = params.getBlue();

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color color = pixelReader.getColor(x, y);

            // Adjust color values based on parameters
            double adjustedRed = color.getRed() * red;
            double adjustedGreen = color.getGreen() * green;
            double adjustedBlue = color.getBlue() * blue;

            // Calculate intensity metrics
            double intensity = (adjustedRed + adjustedGreen + adjustedBlue) / 3.0;
            double purpleIntensity = (adjustedRed + adjustedBlue) / 2.0 - adjustedGreen;

            // Improved WBC detection using multiple criteria
            boolean isWBC = intensity < 0.4 && // Dark objects
                           purpleIntensity > 0.15 && // Purple tint
                           color.getSaturation() > 0.2; // Some color saturation

            // Improved RBC detection
            boolean isRBC = intensity < 0.8 && // Not background
                           intensity > 0.4 && // Not too dark
                           purpleIntensity > 0.1; // Some purple tint

            // Apply brightness adjustment
            intensity += brightness;

            // Color coding the results
            if (isWBC) {
                // Dark purple for WBCs
                pixelWriter.setColor(x, y, Color.rgb(75, 0, 130));
            } else if (isRBC) {
                // Light purple/pink for RBCs
                pixelWriter.setColor(x, y, Color.rgb(219, 112, 147));
            } else {
                // White for background
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }
    }

    return processedImage;
}

@Override
public Image processSecondaryImage(Image originalImage) {
    int width = (int) originalImage.getWidth();
    int height = (int) originalImage.getHeight();

    WritableImage binaryImage = new WritableImage(width, height);
    PixelReader pixelReader = originalImage.getPixelReader();
    PixelWriter pixelWriter = binaryImage.getPixelWriter();

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color color = pixelReader.getColor(x, y);

            // Enhanced detection metrics
            double intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
            double purpleIntensity = (color.getRed() + color.getBlue()) / 2.0 - color.getGreen();

            // Improved WBC detection criteria
            boolean isWBC = intensity < 0.4 &&
                          purpleIntensity > 0.15 &&
                          color.getSaturation() > 0.2;

            // Improved RBC detection criteria
            boolean isRBC = intensity < 0.8 &&
                          intensity > 0.4 &&
                          purpleIntensity > 0.1;

            if (isWBC) {
                pixelWriter.setColor(x, y, Color.RED); // WBCs in red
            } else if (isRBC) {
                pixelWriter.setColor(x, y, Color.BLUE); // RBCs in blue
            } else {
                pixelWriter.setColor(x, y, Color.WHITE); // Background in white
            }
        }
    }

    return binaryImage;
}


    private void findObjectsOfType(PixelReader pixelReader, boolean[][] visited,
                                 List<BoundingBox> objects, ObjectType type) {
        int objectsFound = 0;

        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                if (!visited[x][y] && isObjectPixel(pixelReader.getColor(x, y), type)) {
                    List<Point2D> region = findConnectedRegion(x, y, pixelReader, visited,
                                                             originalWidth, originalHeight, type);
                    if (region.size() > 50) { // Minimum size threshold
                        objectsFound++;
                        BoundingBox bbox = calculateBoundingBox(region);
                        objects.add(bbox);
                        System.out.println(type + " Object " + objectsFound + " found: " +
                                "x=" + bbox.getMinX() +
                                ", y=" + bbox.getMinY() +
                                ", width=" + bbox.getWidth() +
                                ", height=" + bbox.getHeight());
                    }
                }
            }
        }
        System.out.println("Total " + type + " objects found: " + objectsFound);
    }

    private boolean isObjectPixel(Color color, ObjectType type) {
        double brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;

        switch (type) {
            case TYPE_W:
                return brightness < 0.3;  // Adjust threshold for dark objects
            case TYPE_R:
                return brightness > 0.7;  // Adjust threshold for light objects
            default:
                return false;
        }
    }

    private List<Point2D> findConnectedRegion(int startX, int startY, PixelReader pixelReader,
                                            boolean[][] visited, int width, int height,
                                            ObjectType type) {
        List<Point2D> region = new ArrayList<>();
        Queue<Point2D> queue = new LinkedList<>();

        queue.add(new Point2D(startX, startY));
        visited[startX][startY] = true;

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        while (!queue.isEmpty()) {
            Point2D current = queue.poll();
            region.add(current);

            int x = (int) current.getX();
            int y = (int) current.getY();

            for (int i = 0; i < 8; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    if (!visited[newX][newY] &&
                        isObjectPixel(pixelReader.getColor(newX, newY), type)) {
                        queue.add(new Point2D(newX, newY));
                        visited[newX][newY] = true;
                    }
                }
            }
        }

        return region;
    }

    private BoundingBox calculateBoundingBox(List<Point2D> region) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point2D p : region) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY,
                originalWidth, originalHeight);
    }

    private void drawBoundingBoxes(PixelWriter pixelWriter, List<BoundingBox> boxes) {
        Color boxColor = Color.BLUE;
        int lineThickness = 5;
        for (BoundingBox box : boxes) {
            drawBox(pixelWriter, box, boxColor, lineThickness);
        }
    }

    private void drawBox(PixelWriter pixelWriter, BoundingBox box, Color boxColor, int lineThickness) {
        int x = (int) box.getMinX();
        int y = (int) box.getMinY();
        int width = (int) box.getWidth();
        int height = (int) box.getHeight();

        // Draw horizontal lines
        for (int i = Math.max(0, x); i < Math.min(x + width, originalWidth); i++) {
            for (int t = 0; t < lineThickness; t++) {
                // Top border
                int topY = y + t;
                if (topY >= 0 && topY < originalHeight) {
                    pixelWriter.setColor(i, topY, boxColor);
                }

                // Bottom border
                int bottomY = y + height - t;
                if (bottomY >= 0 && bottomY < originalHeight) {
                    pixelWriter.setColor(i, bottomY, boxColor);
                }
            }
        }

        // Draw vertical lines
        for (int i = Math.max(0, y); i < Math.min(y + height, originalHeight); i++) {
            for (int t = 0; t < lineThickness; t++) {
                // Left border
                int leftX = x + t;
                if (leftX >= 0 && leftX < originalWidth) {
                    pixelWriter.setColor(leftX, i, boxColor);
                }

                // Right border
                int rightX = x + width - t;
                if (rightX >= 0 && rightX < originalWidth) {
                    pixelWriter.setColor(rightX, i, boxColor);
                }
            }
        }
    }



    @Override
    public Image processImage(Image originalImage) {
        return processImage(originalImage, new ProcessingParameters(0, 0, 0, 1, 1, 1));
    }

    @Override
    public String getProcessorName() {
        return "BloodCell Detection";
    }
}
