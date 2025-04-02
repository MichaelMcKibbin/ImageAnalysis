package com.michaelmckibbin.imageanalysis;

public class ProcessingParameters {
    private double brightness;
    private double saturation;
    private double hue;
    private double red;
    private double green;
    private double blue;
    private double redCellThreshold;
    private double whiteCellThreshold;
    private double minCellSize;
    private double maxCellSize;

    // Constructor
    public ProcessingParameters(double brightness, double saturation, double hue,
                              double red, double green, double blue, double redCellThreshold,
                              double whiteCellThreshold, double minCellSize, double maxCellSize) {
        this.brightness = brightness;
        this.saturation = saturation;
        this.hue = hue;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.redCellThreshold = redCellThreshold;
        this.whiteCellThreshold = whiteCellThreshold;
        this.minCellSize = minCellSize;
        this.maxCellSize = maxCellSize;
    }
    public ProcessingParameters(double brightness, double saturation, double hue,
                                double red, double green, double blue) {
        this.brightness = brightness;
        this.saturation = saturation;
        this.hue = hue;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.redCellThreshold = 0.2;
        this.whiteCellThreshold = 0.5;
        this.minCellSize = 10;
        this.maxCellSize = 100;
    }
    // Static factory methods for default parameters per processor type
    public static ProcessingParameters getDefaultBlackAndWhite() {
        return new ProcessingParameters(
                0.25,    // brightness - adjusted for better initial detail
                0.0,     // saturation
                0.0,     // hue
                1.5,     // red - increased for better initial visibility
                1.5,     // green
                1.5      // blue

        );
    }

    public static ProcessingParameters getDefaultGrayscale() {
        return new ProcessingParameters(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    public static ProcessingParameters getDefaultSepia() {
        return new ProcessingParameters(0.0, 0.2, 0.0, 1.2, 1.0, 0.8);
    }

    public static ProcessingParameters getDefaultBloodCellDetection() {
        return new ProcessingParameters(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    public static ProcessingParameters getDefaultTricolourBlood() {double  redCellThreshold = 0.2; // Example threshold, adjust as needed
        double whiteCellThreshold = 0.5; // Example threshold, adjust as needed
        double minCellSize = 10; // Example minimum cell size, adjust as needed
        double maxCellSize = 100; // Example maximum cell size, adjust as needed

        return new ProcessingParameters(
                0.0,    // brightness
                0.0,    // saturation
                0.0,    // hue
                1.0,    // red
                1.0,    // green
                1.0     // blue
                ,redCellThreshold, whiteCellThreshold, minCellSize, maxCellSize

        );
    }

    // Getters
    public double getBrightness() { return brightness; }
    public double getSaturation() { return saturation; }
    public double getHue() { return hue; }
    public double getRed() { return red; }
    public double getGreen() { return green; }
    public double getBlue() { return blue; }
    public double getRedCellThreshold() { return redCellThreshold; }
    public double getWhiteCellThreshold() { return whiteCellThreshold; }
    public double getMinCellSize() { return minCellSize; }
    public double getMaxCellSize() { return maxCellSize; }

}

