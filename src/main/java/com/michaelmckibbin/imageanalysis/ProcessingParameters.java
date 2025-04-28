package com.michaelmckibbin.imageanalysis;

/**
 * Encapsulates parameters for image processing and cell detection in blood cell analysis.
 * This class holds values for both image adjustment and cell detection thresholds.
 *
 * Image adjustment parameters include:
 * - brightness: Adjusts overall image brightness
 * - saturation: Controls color saturation
 * - hue: Adjusts color hue
 * - red: Controls red channel intensity
 * - green: Controls green channel intensity
 * - blue: Controls blue channel intensity
 *
 * Cell detection parameters include:
 * - redCellThreshold: Sensitivity for detecting red blood cells (0.0 to 1.0)
 *   Lower values increase detection sensitivity but may increase false positives
 * - whiteCellThreshold: Sensitivity for detecting white blood cells (0.0 to 1.0)
 *   Middle values (0.5) provide balanced detection between sensitivity and accuracy
 * - minCellSize: Minimum pixel size for cell detection (1 to 1000)
 *   Helps filter out noise and artifacts
 *
 * @see BloodCellProcessor
 * @see ImageProcessor
 */

/**
 * Reasoning behind red & white threshold levels.
 *
 * Red level
 * A lower value (like 0.2) means:
 * - More lenient detection of red/pink cells
 * - Will detect cells with less intense red coloring
 * - May result in more red cells being detected
 * - Could increase false positives
 * Higher values (like 0.5) would:
 * - Be more strict in red cell detection
 * - Only detect cells with stronger red coloring
 * - May miss some fainter red cells
 * - Reduces false positives
 *
 * 0.1 = Very sensitive - detects many red cells
 * 0.2 = Current setting - moderately sensitive
 * 0.3 = Less sensitive
 * 0.4 = Strict - only very clear red cells
 *
 *
 * White level
 * A middle value (like 0.5) means:
 * - Balanced detection of purple/white cells
 * - Detects cells with moderate purple intensity
 * - Provides good discrimination between white and red cells
 * Lower values would:
 * - Increase sensitivity to white cells
 * - Detect more faintly purple cells
 * - Risk detecting some red cells as white cells
 * Higher values would:
 * - Be more strict in white cell detection
 * - Only detect very distinctly purple cells
 * - Might miss some valid white cells
 *
 * 0.3 = Very sensitive - detects many white cells
 * 0.5 = Default setting - balanced detection
 * 0.6 = Moderate sensitivity - may detect some faint white cells
 * 0.7 = Less sensitive
 * 0.8 = Strict - only very clear white cells
 *
 *
 * Works with isCellOfType() method where color analysis is performed.
 */

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

    public ProcessingParameters() {

    }


    //Getters
    public double getBrightness() {
        return brightness;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getHue() {
        return hue;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public double getRedCellThreshold() {
        return redCellThreshold;
    }

    public double getWhiteCellThreshold() {
        return whiteCellThreshold;
    }

    public double getMinCellSize() {
        return minCellSize;
    }

    public double getMaxCellSize() {
        return maxCellSize;
    }

    //Setters
// Setters
    public void setBrightness(double brightness) {
        if (brightness < 0.0 || brightness > 100.0) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100");
        }
        this.brightness = brightness;
    }

    public void setSaturation(double saturation) {
        if (saturation < 0.0 || saturation > 100.0) {
            throw new IllegalArgumentException("Saturation must be between 0 and 100");
        }
        this.saturation = saturation;
    }

    public void setHue(double hue) {
        if (hue < 0.0 || hue > 360.0) {
            throw new IllegalArgumentException("Hue must be between 0 and 360");
        }
        this.hue = hue;
    }

    public void setRed(double red) {
        if (red < 0.0 || red > 100.0) {
            throw new IllegalArgumentException("Red value must be between 0 and 100");
        }
        this.red = red;
    }

    public void setGreen(double green) {
        if (green < 0.0 || green > 100.0) {
            throw new IllegalArgumentException("Green value must be between 0 and 100");
        }
        this.green = green;
    }

    public void setBlue(double blue) {
        if (blue < 0.0 || blue > 100.0) {
            throw new IllegalArgumentException("Blue value must be between 0 and 100");
        }
        this.blue = blue;
    }

    public void setRedCellThreshold(double redCellThreshold) {
        if (redCellThreshold < 0.0 || redCellThreshold > 100.0) {
            throw new IllegalArgumentException("Red cell threshold must be between 0 and 100");
        }
        this.redCellThreshold = redCellThreshold;
    }

    public void setWhiteCellThreshold(double whiteCellThreshold) {
        if (whiteCellThreshold < 0.0 || whiteCellThreshold > 100.0) {
            throw new IllegalArgumentException("White cell threshold must be between 0 and 100");
        }
        this.whiteCellThreshold = whiteCellThreshold;
    }

    public void setMinCellSize(double minCellSize) {
        if (minCellSize < 0.0 || minCellSize > 100.0) {
            throw new IllegalArgumentException("Minimum cell size must be between 0 and 100");
        }
        this.minCellSize = minCellSize;
    }
    public void setMaxCellSize(double maxCellSize) {
        if (maxCellSize < 0 || maxCellSize > 20000) {
            throw new IllegalArgumentException("Maximum cell size must be between 0 and 20000");
        }
        this.maxCellSize = maxCellSize;
    }

    public ProcessingParameters(
            double brightness,
            double saturation,
            double hue,
            double red,
            double green,
            double blue,
            double redCellThreshold,
            double whiteCellThreshold,
            double minCellSize,
            double maxCellSize) {

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
    // Static factory methods for default parameters per processor type
    public static ProcessingParameters getDefaultBlackAndWhite() {
        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                0.0,  // redCellThreshold
                0.0,  // whiteCellThreshold
                0.0,   // minCellSize
                5000 //maxCellSize
        );
    }

    public static ProcessingParameters getDefaultGrayscale() {
        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                0.0,  // redCellThreshold
                0.0,  // whiteCellThreshold
                0.0,   // minCellSize
                5000 //maxCellSize
        );
    }

    public static ProcessingParameters getDefaultSepia() {
        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                0.0,  // redCellThreshold
                0.0,  // whiteCellThreshold
                0.0,   // minCellSize
                5000 //maxCellSize
        );
    }

    public static ProcessingParameters getDefaultBloodCellDetection() {
        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                0.1,  // redCellThreshold // 0.2 = More sensitive detection of red cells
                0.5,  // whiteCellThreshold // 0.5 = Moderate sensitivity for white cells
                0.0,   // minCellSize
                5000 //maxCellSize
        );
    }

    public static ProcessingParameters getDefaultTricolourBlood() {double  redCellThreshold = 0.2; // Example threshold, adjust as needed
        double whiteCellThreshold = 0.5; // adjust as needed
        double minCellSize = 10; // adjust as needed
        double maxCellSize = 100; // adjust as needed

        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                redCellThreshold,
                whiteCellThreshold,
                minCellSize,
                maxCellSize
        );
    }

    public static ProcessingParameters UnionFindBloodCellProcessor() {
        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                5.0,  // redCellThreshold
                0.5,  // whiteCellThreshold
                10.0,   // minCellSize
                5000 //maxCellSize
        );
    }
}

