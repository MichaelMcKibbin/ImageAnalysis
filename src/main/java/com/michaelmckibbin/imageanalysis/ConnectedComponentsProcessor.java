package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.Consumer;

/**
 * An alternate method of blood cell detection using the Connected Components detection.
 *
 * The TricolourBloodProcessor is used to create a binary image, and then the ConnectedComponentsProcessor
 * is used to detect the connected components in the binary image. The connected components are then
 * used to detect the blood cells.
 *
 * TricolourBloodProcessor uses a UnionFind/disjoint sets approach.
 */
public class ConnectedComponentsProcessor implements ImageProcessor {
    private int width;
    private int height;
    private int[][] labels;
    private int nextLabel = 1;
    private Consumer<Image> resultCallback;

    @Override
    public String getProcessorName() {
        return "Connected Components Processor";
    }

    public void setResultCallback(Consumer<Image> callback) {
        this.resultCallback = callback;
    }

    @Override
    public Image processImage(Image image, ProcessingParameters params) {
        System.out.println("\n******************************************************");
        System.out.println("\n* Processing image with ConnectedComponentsProcessor *");
        System.out.println("\n******************************************************");
        System.out.println("\n");
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        this.labels = new int[height][width];

        // Create a WritableImage for the initial result
        WritableImage initialResult = new WritableImage(width, height);

        // Create TricolourBloodProcessor
        TricolourBloodProcessor triProcessor = new TricolourBloodProcessor();

        // Set callback to process the image after TricolourBloodProcessor finishes
        triProcessor.setImageDisplayCallback(processedImage -> {
            // Now process the fully processed image from TricolourBloodProcessor
            Image result = processImage(image, processedImage);
            if (resultCallback != null) {
                resultCallback.accept(result);
            }
        });

        // Start the TricolourBloodProcessor and return initial result
        triProcessor.processImage(image, params);
        return initialResult;
    }

    private Image processImage(Image originalImage, Image processedImage) {
        WritableImage resultImage = new WritableImage(width, height);
        PixelReader processedReader = processedImage.getPixelReader();
        PixelReader originalReader = originalImage.getPixelReader();
        PixelWriter writer = resultImage.getPixelWriter();

        // Debug: Print some sample colors from processed image
        System.out.println("\nSample colors from processed image:");
        for (int y = 0; y < height; y += 100) {
            for (int x = 0; x < width; x += 100) {
                Color color = processedReader.getColor(x, y);
                if (!color.equals(Color.WHITE)) {
                    System.out.printf("Color at (%d,%d): R=%.2f, G=%.2f, B=%.2f%n",
                        x, y, color.getRed(), color.getGreen(), color.getBlue());
                }
            }
        }

        // First pass: Label connected components
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = processedReader.getColor(x, y);
                if (isCell(color)) {
                    labelPixel(x, y, "Cell", color);
                }
            }
        }

        // Create cell map
        Map<Integer, Cell> cellMap = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = labels[y][x];
                if (label > 0) {
                    Cell cell = cellMap.computeIfAbsent(label, k -> new Cell("Cell"));
                    cell.updateBounds(x, y);
                }
            }
        }

        System.out.println("Found " + cellMap.size() + " cells");

        // Draw original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, originalReader.getColor(x, y));
            }
        }

        // Draw blue rectangles
        drawCellBoundaries(writer, cellMap.values());

        return resultImage;
    }

    private void labelPixel(int x, int y, String cellType, Color color) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        Set<Integer> neighborLabels = getNeighborLabels(x, y);

        if (neighborLabels.isEmpty()) {
            labels[y][x] = nextLabel;
            nextLabel++;
        } else {
            labels[y][x] = neighborLabels.iterator().next();
        }
    }

    private Set<Integer> getNeighborLabels(int x, int y) {
        Set<Integer> neighbors = new HashSet<>();

        // Check 8-connected neighbors
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int newX = x + dx;
                int newY = y + dy;

                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    int label = labels[newY][newX];
                    if (label > 0) {
                        neighbors.add(label);
                    }
                }
            }
        }

        return neighbors;
    }

    private void drawCellBoundaries(PixelWriter writer, Collection<Cell> cells) {
        Color boundaryColor = Color.BLUE;
        int borderThickness = 2;

        System.out.println("Drawing boundaries for " + cells.size() + " cells");
        int validCells = 0;

        for (Cell cell : cells) {
            // Only draw rectangles for cells that are large enough (to avoid noise)
            int width = cell.maxX - cell.minX;
            int height = cell.maxY - cell.minY;
            if (width < 5 || height < 5) continue;

            validCells++;

            // Draw horizontal lines (top and bottom)
            for (int x = cell.minX; x <= cell.maxX; x++) {
                for (int t = 0; t < borderThickness; t++) {
                    // Draw top border
                    if (cell.minY - t >= 0) {
                        writer.setColor(x, cell.minY - t, boundaryColor);
                    }
                    // Draw bottom border
                    if (cell.maxY + t < this.height) {
                        writer.setColor(x, cell.maxY + t, boundaryColor);
                    }
                }
            }

            // Draw vertical lines (left and right)
            for (int y = cell.minY; y <= cell.maxY; y++) {
                for (int t = 0; t < borderThickness; t++) {
                    // Draw left border
                    if (cell.minX - t >= 0) {
                        writer.setColor(cell.minX - t, y, boundaryColor);
                    }
                    // Draw right border
                    if (cell.maxX + t < this.width) {
                        writer.setColor(cell.maxX + t, y, boundaryColor);
                    }
                }
            }
        }

        System.out.println("Drew boundaries for " + validCells + " valid cells");
    }

/**
 * Determines if a pixel's color indicates the presence of a specific cell type.
 * Uses different thresholds for white and red blood cells based on
 * their staining characteristics.
 *
 */
    private boolean isCell(Color color) {
        return isPurple(color) || isRed(color);
    }

    private boolean isPurple(Color color) {
        double tolerance = 0.01;
        return Math.abs(color.getRed() - 75.0/255.0) < tolerance &&
               Math.abs(color.getGreen() - 0.0) < tolerance &&
               Math.abs(color.getBlue() - 130.0/255.0) < tolerance;
    }

    private boolean isRed(Color color) {
        double tolerance = 0.01;
        return Math.abs(color.getRed() - 219.0/255.0) < tolerance &&
               Math.abs(color.getGreen() - 112.0/255.0) < tolerance &&
               Math.abs(color.getBlue() - 147.0/255.0) < tolerance;
    }

    private static class Cell {
        String type;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        public Cell(String type) {
            this.type = type;
        }

        public void updateBounds(int x, int y) {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
    }

    @Override
    public Image processImage(Image image) {
        return null;
    }
}
