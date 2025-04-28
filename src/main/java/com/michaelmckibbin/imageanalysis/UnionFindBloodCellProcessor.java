package com.michaelmckibbin.imageanalysis;

import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.stream.Collectors;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.michaelmckibbin.imageanalysis.UnionFind;
import javafx.scene.text.Font;

import java.util.LinkedList;
//import com.michaelmckibbin.imageanalysis.LinkedList; // need to add Queue...

/**
 * Processes blood cell images using a Union-Find algorithm to detect and classify blood cells.
 * This processor identifies two types of blood cells:
 * <ul>
 *     <li>White blood cells (appearing as purple in stained samples)</li>
 *     <li>Red blood cells (appearing as pink in stained samples)</li>
 * </ul>
 * The processor uses colour thresholds and connected component (pixel grouping) analysis to identify cell clusters.
 */

public class UnionFindBloodCellProcessor implements ImageProcessor{

    private ProcessingMetrics metrics = new ProcessingMetrics();
    // Getter
    public ProcessingMetrics getMetrics() {
        return metrics;
    }

    /** Threshold value for detecting white blood cells (purple/darker objects) */
    private double whiteCellThreshold;  // For purple/darker objects

    /** Threshold value for detecting red blood cells (dark pink objects) */
    private double redCellThreshold;    // For dark pink objects

    /** Minimum size (in pixels) for a valid cell cluster */
    private int minCellSize;           // Will be set from slider
    //private static final int DEFAULT_MIN_CELL_SIZE = 500;  // Default minimum size - changed to variable

    /** Maximum size (in pixels) for a valid cell cluster to prevent false positives */
    private int maxCellSize;     // Will be set from slider


    /**
     * Enumeration of cell types that can be detected by the processor.
     */
    private enum CellType {
        /** Represents white blood cells, typically appearing purple in stained samples */
        WHITE_CELL,  // Purple coloured cells (typically darker)

        /** Represents red blood cells, typically appearing pink in stained samples */
        RED_CELL     // pink coloured cells
    }


    /**
     * Processes an image to detect and mark blood cells using specified parameters.
     *
     * @param originalImage The source image to be processed
     * @param params Processing parameters containing thresholds and other settings
     * @return A new Image with detected cells marked: blue for white blood cells, green for red blood cells
     */
@Override
    public Image processImage(Image originalImage, ProcessingParameters params) {
            long startTotal = System.nanoTime();

    // Debug all incoming parameter values
    System.out.println("\n*****************************************************");
    System.out.println("\n* Processing image with UnionFindBloodCellProcessor *");
    System.out.println("\n*****************************************************");
    System.out.println("\n");
    System.out.println("\nIncoming Parameter Values:");
    System.out.println("White Cell Threshold: " + params.getWhiteCellThreshold());
    System.out.println("Red Cell Threshold: " + params.getRedCellThreshold());
    System.out.println("Min Cell Size: " + params.getMinCellSize());
    System.out.println("Max Cell Size: " + params.getMaxCellSize());

    // Parameter initialization timing
    long startParams = System.nanoTime();

    minCellSize = 1 + (int)(params.getMinCellSize() / 100.0 * 999);
    maxCellSize = (int)(100 + (params.getMaxCellSize() / 100.0 * (20000 - 100)));


    whiteCellThreshold = params.getWhiteCellThreshold() / 100.0;
    redCellThreshold = params.getRedCellThreshold() / 100.0;

    long endParams = System.nanoTime();

    // Image copy timing
    long startCopy = System.nanoTime();
    WritableImage processedImage = copyOriginalImage(originalImage);
    long endCopy = System.nanoTime();

    // White cell detection timing
    long startWhiteCells = System.nanoTime();
    List<Rectangle> whiteCells = detectCells(originalImage, UnionFindBloodCellProcessor.CellType.WHITE_CELL);
    long endWhiteCells = System.nanoTime();

    // Red cell detection timing
    long startRedCells = System.nanoTime();
    List<Rectangle> redCells = detectCells(originalImage, UnionFindBloodCellProcessor.CellType.RED_CELL);
    long endRedCells = System.nanoTime();

    // Cell marking timing
    long startMarking = System.nanoTime();
    markCells(processedImage, whiteCells, Color.DARKRED);
    markCells(processedImage, redCells, Color.DARKBLUE);
    long endMarking = System.nanoTime();

    // Calculate total time
    long endTotal = System.nanoTime();

    // Print performance metrics
    System.out.println("\nPerformance Metrics:");
    System.out.println("--------------------");
    System.out.printf("Parameter initialization: %.2f ms%n", (endParams - startParams) / 1_000_000.0);
    System.out.printf("Image copy: %.2f ms%n", (endCopy - startCopy) / 1_000_000.0);
    System.out.printf("White cell detection: %.2f ms%n", (endWhiteCells - startWhiteCells) / 1_000_000.0);
    System.out.printf("Red cell detection: %.2f ms%n", (endRedCells - startRedCells) / 1_000_000.0);
    System.out.printf("Cell marking: %.2f ms%n", (endMarking - startMarking) / 1_000_000.0);
    System.out.printf("Total processing time: %.2f ms%n", (endTotal - startTotal) / 1_000_000.0);
    System.out.println();
    // Print averages of metrics
    metrics.printAverages();
    System.out.println();

    // Print detection results
    System.out.println("\nDetection Results:");
    System.out.println("White cells detected (Purple Dye): " + whiteCells.size());
    System.out.println("Red cells detected: (Pink Dye) " + redCells.size());

    // Calculate cells per second
    double totalTimeSeconds = (endTotal - startTotal) / 1_000_000_000.0;
    int totalCells = whiteCells.size() + redCells.size();
    double cellsPerSecond = totalCells / totalTimeSeconds;
    System.out.printf("Processing speed: %.1f cells/second%n", cellsPerSecond);

        metrics.addMetrics(
                (endWhiteCells - startWhiteCells) / 1_000_000.0,
                (endRedCells - startRedCells) / 1_000_000.0,
                (endTotal - startTotal) / 1_000_000.0,
                cellsPerSecond
        );

    return processedImage;

}



    /**
     * Processes an image using default blood cell detection parameters.
     *
     * @param originalImage The source image to be processed
     * @return A new Image with detected cells marked using default parameters
     */
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
                0.5,    // redCellThreshold
                50.0,    // whiteCellThreshold
                0.0,   // minCellSize
                5000 //maxCellSize
        );
        return processImage(originalImage, defaultParams);
    }


/**
 * Checks if a pixel belongs to the specified cell type based on its colour.
 *
 * @param image The source image
 * @param x X coordinate of the pixel
 * @param y Y coordinate of the pixel
 * @param type The type of cell to check for (WHITE_CELL or RED_CELL)
 * @return true if the pixel is part of the specified cell type
 */
private boolean isCellPixel(Image image, int x, int y, CellType type) {
    if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
        return false;
    }

    Color color = image.getPixelReader().getColor(x, y); // US spelling to match Java spelling
    return isCellOfType(color, type);
}

/**
 * Creates a bounding rectangle from a list of points.
 *
 * @param points List of points belonging to a cell
 * @return Rectangle that bounds all the points
 */
private Rectangle getBoundingRectangle(List<Point> points) {
    if (points.isEmpty()) {
        return null;
    }

    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double maxY = Double.MIN_VALUE;

    for (Point p : points) {
        minX = Math.min(minX, p.getX());
        minY = Math.min(minY, p.getY());
        maxX = Math.max(maxX, p.getX());
        maxY = Math.max(maxY, p.getY());
    }

    double width = maxX - minX;
    double height = maxY - minY;

    return new Rectangle(minX, minY, width, height);
}

    /**
     * Determines if a colour matches the characteristics of the specified cell type.
     * For white blood cells, checks for darker purple colouring.
     * For red blood cells, checks for pink/red colouring with specific brightness constraints.
     *
     * @param color The color (colour) to analyze // US spelling to match Java
     * @param type The type of cell to check for (WHITE_CELL or RED_CELL)
     * @return true if the colour matches the specified cell type's characteristics
     */


// copy of original - before trying HSV...
//
// private boolean isCellOfType(Color color, CellType type) {
//    double brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
//    double redComponent = color.getRed();
//    double blueComponent = color.getBlue();
//
//    switch (type) {
//        case WHITE_CELL:
//            // White blood cells are typically darker (purple)
//            // Check if the pixel is dark enough and has more blue component
//            return brightness < whiteCellThreshold && blueComponent > redComponent;
//
//        case RED_CELL:
//            // Red blood cells are typically pink/red
//            // Check if the pixel has strong red component but isn't too bright
//            return redComponent > redCellThreshold &&
//                   redComponent > blueComponent &&
//                   brightness < 0.8;
//
//        default:
//            return false;
//    }
//}

    private boolean isCellOfType(Color color, CellType type) {
        double brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
        double redComponent = color.getRed();
        double blueComponent = color.getBlue();

        switch (type) {
            case WHITE_CELL:
                // White blood cells are typically darker (purple)
                // Check if the pixel is dark enough and has more blue component
                return brightness < whiteCellThreshold && blueComponent > redComponent;

            case RED_CELL:
                // Red blood cells are typically pink/red
                // Check if the pixel has strong red component but isn't too bright
                return redComponent > redCellThreshold &&
                        redComponent > blueComponent &&
                        brightness < 0.8;

            default:
                return false;
        }
    }

//    // TRY HSV (Hue, Saturation, Value)
//    private boolean isCellOfType(Color color, CellType type) {
//        double brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
//        double[] hsv = rgbToHsv(color.getRed(), color.getGreen(), color.getBlue());
//        double hue = hsv[0];
//        double saturation = hsv[1];
//        double value = hsv[2];
//        int whiteCellPixelCount=0;
//        int redCellPixelCount=0;
//
//        double redComponent = color.getRed();
//        double blueComponent = color.getBlue();
//
//        switch (type) {
//            case WHITE_CELL:
//                // White blood cells are typically darker (purple)
//                // Check if the pixel is dark enough and has more blue component
//            //    return brightness < whiteCellThreshold && blueComponent > redComponent;
//            boolean isWhiteCell = brightness < whiteCellThreshold &&
//                    color.getBlue() > color.getRed();
//            if (isWhiteCell) {
//                whiteCellPixelCount++;
//            }
//            return isWhiteCell;
//
//            case RED_CELL:
//                boolean isRedCell =
//                        // starting values
//                        // ((hue >= 300 && hue <= 360) || (hue >= 0 && hue <= 30)) && // Red/Pink hue range
//                        //                                saturation >= 0.15 &&  // Minimum saturation to avoid white
//                        //                                saturation <= 0.85 &&  // Maximum saturation to include pink
//                        //                                value >= 0.3 &&        // Not too dark
//                        //                                value <= 0.95;         // Not too light
//
//                        ((hue >= 300 && hue <= 360) || (hue >= 0 && hue <= 30)) && // Red/Pink hue range
//                                saturation >= 0.15 &&  // Minimum saturation to avoid white
//                                saturation <= 0.85 &&  // Maximum saturation to include pink
//                                value >= 0.3 &&        // Not too dark
//                                value <= 0.95;         // Not too light
//
//                if (isRedCell) {
//                    redCellPixelCount++;
//                }
//                return isRedCell;
//
//            default:
//                return false;
//        }
//    }
//
//
//    // Helper method to convert RGB to HSV
//    private double[] rgbToHsv(double r, double g, double b) {
//        r /= 255.0;
//        g /= 255.0;
//        b /= 255.0;
//
//        double max = Math.max(Math.max(r, g), b);
//        double min = Math.min(Math.min(r, g), b);
//        double delta = max - min;
//
//        double hue = 0;
//        if (delta != 0) {
//            if (max == r) {
//                hue = 60 * (((g - b) / delta) % 6);
//            } else if (max == g) {
//                hue = 60 * ((b - r) / delta + 2);
//            } else {
//                hue = 60 * ((r - g) / delta + 4);
//            }
//        }
//        if (hue < 0) hue += 360;
//
//        double saturation = (max == 0) ? 0 : delta / max;
//        double value = max;
//
//        return new double[]{hue, saturation, value};
//    }
//
// end of Try HSV
//=======================

    /**
     * Detects cells of the specified type in the image using connected component analysis.
     *
     * @param image The source image to analyze
     * @param cellType The type of cells to detect (WHITE_CELL or RED_CELL)
     * @return List of Rectangles representing the bounding boxes of detected cells
     */
private List<Rectangle> detectCells(Image image, CellType cellType) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    UnionFind uf = new UnionFind(width * height);

    // First pass: Union adjacent pixels that belong to the same cell
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            if (isCellPixel(image, x, y, cellType)) {
                int p = y * width + x;

                // Check neighboring pixels (4-connectivity)
                if (x > 0 && isCellPixel(image, x-1, y, cellType)) {
                    uf.union(p, p-1);
                }
                if (y > 0 && isCellPixel(image, x, y-1, cellType)) {
                    uf.union(p, p-width);
                }
            }
        }
    }

    // Second pass: Collect cells that meet the size threshold
    Map<Integer, List<Point>> cellGroups = new HashMap<>();
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int p = y * width + x;
            if (isCellPixel(image, x, y, cellType)) {
                int root = uf.find(p);
                if (uf.getSize(root) >= minCellSize &&
                    uf.getSize(root) <= maxCellSize) {
                    cellGroups.computeIfAbsent(root, k -> new ArrayList<>())
                             .add(new Point(x, y));
                }
            }
        }
    }

    // Convert cell groups to bounding rectangles
    return cellGroups.values().stream()
            .map(this::getBoundingRectangle)
            .collect(Collectors.toList());
}

    /**
     * Performs a flood fill operation starting from a given point to identify a complete cell.
     *
     * @param startX The starting X coordinate
     * @param startY The starting Y coordinate
     * @param image The image being analyzed
     * @param visited Array tracking visited pixels
     * @param type The type of cell being detected
     * @return Rectangle representing the bounding box of the detected cell
     */
    private Rectangle floodFill(int startX, int startY, Image image, boolean[][] visited, UnionFindBloodCellProcessor.CellType type) {
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

//    private void markCells(WritableImage image, List<Rectangle> cells, Color color) {
//        for (Rectangle cell : cells) {
//            drawRectangle(image, cell, color);
//        }
//    }

    private void markCells(WritableImage image, List<Rectangle> cells, Color color) {
        // Create a Canvas to overlay text
        Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the original image onto the canvas
        gc.drawImage(image, 0, 0);

        // Set up text properties
        gc.setFill(color);
        gc.setFont(new Font("Arial", 18)); // Adjust font and size as needed

        // Draw rectangles and numbers
        for (int i = 0; i < cells.size(); i++) {
            Rectangle cell = cells.get(i);
            drawRectangle(image, cell, color);

            // Draw cell number
            String number = String.valueOf(i + 1);
            gc.fillText(number,
                    cell.getX() + 10, // Position slightly inside top left corner of the rectangle. (Negative value is outside and above)
                    cell.getY() + 25); // Position slightly inside top left corner of the rectangle. (Negative value is outside and above)
        }

        // Convert canvas back to WritableImage
        SnapshotParameters params = new SnapshotParameters();
        WritableImage newImage = canvas.snapshot(params, null);

        // Copy the text overlay back to the original image
        PixelWriter writer = image.getPixelWriter();
        PixelReader reader = newImage.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixelColor = reader.getColor(x, y);
                // Only copy non-transparent pixels (the text)
                if (pixelColor.getOpacity() > 0) {
                    writer.setColor(x, y, pixelColor);
                }
            }
        }
    }


    // Helper methods for drawing...
    private void drawRectangle(WritableImage image, Rectangle rect, Color color) {
        PixelWriter writer = image.getPixelWriter();
        int x = (int) rect.getX();
        int y = (int) rect.getY();
        int width = (int) rect.getWidth();
        int height = (int) rect.getHeight();
        int thickness = 2;

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


    /**
     * Returns the name of this image processor.
     *
     * @return The processor name as a String
     */
    @Override
    public String getProcessorName() {
        return "Union Find Blood Analysis";
    }

    /**
     * Returns a string representation of this processor.
     *
     * @return The processor name as a String
     */
    @Override
    public String toString() {
        return "UnionFind Blood Cell Processor";
    }
}