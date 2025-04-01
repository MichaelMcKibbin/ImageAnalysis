package com.michaelmckibbin.imageanalysis;

public class ProcessingParameters {
    private double brightness;
    private double saturation;
    private double hue;
    private double red;
    private double green;
    private double blue;

    // Constructor
    public ProcessingParameters(double brightness, double saturation, double hue,
                              double red, double green, double blue) {
        this.brightness = brightness;
        this.saturation = saturation;
        this.hue = hue;
        this.red = red;
        this.green = green;
        this.blue = blue;
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

    public static ProcessingParameters getDefaultTricolourBlood() {
        return new ProcessingParameters(
                0.0,    // brightness
                0.0,    // saturation
                0.0,    // hue
                1.0,    // red
                1.0,    // green
                1.0     // blue
        );
    }

    // Getters
    public double getBrightness() { return brightness; }
    public double getSaturation() { return saturation; }
    public double getHue() { return hue; }
    public double getRed() { return red; }
    public double getGreen() { return green; }
    public double getBlue() { return blue; }
}

