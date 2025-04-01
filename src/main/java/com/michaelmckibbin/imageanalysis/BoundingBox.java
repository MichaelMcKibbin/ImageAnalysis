package com.michaelmckibbin.imageanalysis;

public class BoundingBox {
    private final double minX;
    private final double minY;
    private final double width;
    private final double height;
    private final double imageWidth;
    private final double imageHeight;

    public BoundingBox(double minX, double minY, double width, double height,
                      double imageWidth, double imageHeight) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public double getMinX() { return minX; }
    public double getMinY() { return minY; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getImageWidth() { return imageWidth; }
    public double getImageHeight() { return imageHeight; }
}
