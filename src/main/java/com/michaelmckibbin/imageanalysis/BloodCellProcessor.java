package com.michaelmckibbin.imageanalysis;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.scene.shape.Rectangle;

public class BloodCellProcessor implements ImageProcessor {
    private double whiteCellThreshold;  // For purple/darker objects
    private double redCellThreshold;    // For dark pink objects
    private int minCellSize;           // Will be set from slider
    private int maxCellSize = 5000;     // Maximum size to prevent false positives
    //private static final int DEFAULT_MIN_CELL_SIZE = 500;  // Default minimum size

    private enum CellType {
        WHITE_CELL,  // Purple colored cells (typically darker)
        RED_CELL     // Dark pink colored cells
    }

    @Override
    public Image processImage(Image originalImage, ProcessingParameters params) {

        // Debug all incoming parameter values
        System.out.println("\nIncoming Parameter Values:");
        System.out.println("White Cell Threshold: " + params.getWhiteCellThreshold());
        System.out.println("Red Cell Threshold: " + params.getRedCellThreshold());
        System.out.println("Cell Size Threshold: " + params.getMinCellSize());

        // Convert slider value (0-100) to cell size range (1-1000)
        minCellSize = 1 + (int)(params.getMinCellSize() / 100.0 * 999);
        System.out.println("Calculated minCellSize: " + minCellSize + " pixels");

        // Debug output
        System.out.println("\nSlider Values:");
        System.out.println("Cell Size Threshold slider: " + params.getMinCellSize());
        System.out.println("Minimum Cell Size: " + minCellSize + " pixels");

        whiteCellThreshold = params.getWhiteCellThreshold() / 100.0;
        redCellThreshold = params.getRedCellThreshold() / 100.0;

        WritableImage processedImage = copyOriginalImage(originalImage);

        List<Rectangle> whiteCells = detectCells(originalImage, CellType.WHITE_CELL);
        List<Rectangle> redCells = detectCells(originalImage, CellType.RED_CELL);

        System.out.println("\nDetection Results:");
        System.out.println("White (Purple) cells detected: " + whiteCells.size());
        System.out.println("Red (Dark Pink) cells detected: " + redCells.size());

        markCells(processedImage, whiteCells, Color.BLUE);
        markCells(processedImage, redCells, Color.GREEN);

        return processedImage;
    }

    @Override
    public Image processImage(Image originalImage) {
        // Default parameters when no sliders are used
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


    private boolean isCellOfType(Color pixelColor, CellType type) {
        double red = pixelColor.getRed();
        double green = pixelColor.getGreen();
        double blue = pixelColor.getBlue();

        if (type == CellType.WHITE_CELL) {
            // Look for purple colors (high red and blue, lower green)
            return (red + blue) / 2 > green + whiteCellThreshold
                   && blue > green
                   && red > green;
        } else {
            // Look for dark pink colors (high red, medium-low blue and green)
            return red > (blue + green) / 2 + redCellThreshold
                   && red > 0.3  // Ensure some minimum redness
                   && green < 0.7 // Not too bright
                   && blue < 0.7; // Not too bright
        }
    }

    private List<Rectangle> detectCells(Image image, CellType type) {
        List<Rectangle> cells = new ArrayList<>();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        boolean[][] visited = new boolean[width][height];
        PixelReader reader = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!visited[x][y]) {
                    Color color = reader.getColor(x, y);
                    if (isCellOfType(color, type)) {
                        Rectangle cellBounds = floodFill(x, y, image, visited, type);
                        if (cellBounds != null) {
                            cells.add(cellBounds);
                        }
                    }
                    visited[x][y] = true;
                }
            }
        }
        return cells;
    }

    private Rectangle floodFill(int startX, int startY, Image image, boolean[][] visited, CellType type) {
        Queue<Point2D> queue = new LinkedList<>();
        queue.add(new Point2D(startX, startY));

        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        int pixelCount = 0;
        PixelReader reader = image.getPixelReader();

        while (!queue.isEmpty()) {
            Point2D p = queue.poll();
            int x = (int) p.getX();
            int y = (int) p.getY();

            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()
                || visited[x][y]) {
                continue;
            }

            Color color = reader.getColor(x, y);
            if (!isCellOfType(color, type)) {
                continue;
            }

            visited[x][y] = true;
            pixelCount++;

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            // Add adjacent pixels to queue
            queue.add(new Point2D(x + 1, y));
            queue.add(new Point2D(x - 1, y));
            queue.add(new Point2D(x, y + 1));
            queue.add(new Point2D(x, y - 1));
        }

        if (pixelCount >= minCellSize && pixelCount <= maxCellSize) {
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
        return null;
    }

    private void markCells(WritableImage image, List<Rectangle> cells, Color color) {
        for (Rectangle cell : cells) {
            drawRectangle(image, cell, color);
        }
    }

    // Helper methods for drawing...
    private void drawRectangle(WritableImage image, Rectangle rect, Color color) {
    PixelWriter writer = image.getPixelWriter();
    int x = (int) rect.getX();
    int y = (int) rect.getY();
    int width = (int) rect.getWidth();
    int height = (int) rect.getHeight();
    int thickness = 4;

    // Cast image dimensions to int
    int imageWidth = (int) image.getWidth();
    int imageHeight = (int) image.getHeight();

    // Draw borders
    drawHorizontalLines(writer, x, y, width, height, color, thickness, imageWidth, imageHeight);
    drawVerticalLines(writer, x, y, width, height, color, thickness, imageWidth, imageHeight);
    drawCornerHighlights(writer, x, y, width, height, color, imageWidth, imageHeight);
}

private void drawHorizontalLines(PixelWriter writer, int x, int y, int width, int height,
                               Color color, int thickness, int maxWidth, int maxHeight) {
    for (int t = 0; t < thickness; t++) {
        // Top line
        for (int i = x; i < x + width; i++) {
            if (i >= 0 && i < maxWidth && y + t >= 0 && y + t < maxHeight) {
                writer.setColor(i, y + t, color);
            }
        }
        // Bottom line
        for (int i = x; i < x + width; i++) {
            if (i >= 0 && i < maxWidth && y + height - t >= 0 && y + height - t < maxHeight) {
                writer.setColor(i, y + height - t, color);
            }
        }
    }
}

private void drawVerticalLines(PixelWriter writer, int x, int y, int width, int height,
                             Color color, int thickness, int maxWidth, int maxHeight) {
    for (int t = 0; t < thickness; t++) {
        // Left line
        for (int j = y; j < y + height; j++) {
            if (x + t >= 0 && x + t < maxWidth && j >= 0 && j < maxHeight) {
                writer.setColor(x + t, j, color);
            }
        }
        // Right line
        for (int j = y; j < y + height; j++) {
            if (x + width - t >= 0 && x + width - t < maxWidth && j >= 0 && j < maxHeight) {
                writer.setColor(x + width - t, j, color);
            }
        }
    }
}

private void drawCornerHighlights(PixelWriter writer, int x, int y, int width, int height,
                                Color color, int maxWidth, int maxHeight) {
    int cornerSize = 6;
    for (int i = 0; i < cornerSize; i++) {
        for (int j = 0; j < cornerSize; j++) {
            drawCornerPixel(writer, x + i, y + j, color, maxWidth, maxHeight);  // Top-left
            drawCornerPixel(writer, x + width - i, y + j, color, maxWidth, maxHeight);  // Top-right
            drawCornerPixel(writer, x + i, y + height - j, color, maxWidth, maxHeight);  // Bottom-left
            drawCornerPixel(writer, x + width - i, y + height - j, color, maxWidth, maxHeight);  // Bottom-right
        }
    }
}

private void drawCornerPixel(PixelWriter writer, int x, int y, Color color, int maxWidth, int maxHeight) {
    if (x >= 0 && x < maxWidth && y >= 0 && y < maxHeight) {
        writer.setColor(x, y, color);
    }
}

private WritableImage copyOriginalImage(Image originalImage) {
    int width = (int) originalImage.getWidth();
    int height = (int) originalImage.getHeight();
    WritableImage processedImage = new WritableImage(width, height);
    PixelWriter writer = processedImage.getPixelWriter();
    PixelReader reader = originalImage.getPixelReader();

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            writer.setColor(x, y, reader.getColor(x, y));
        }
    }
    return processedImage;
}



@Override
public String getProcessorName() {
    return "Blood Cell Analysis";
}

}
