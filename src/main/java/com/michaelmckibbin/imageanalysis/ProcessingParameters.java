package com.michaelmckibbin.imageanalysis;

public class ProcessingParameters {
    private final double brightness;
    private final double saturation;
    private final double hue;
    private final double red;
    private final double green;
    private final double blue;
    private final double redCellThreshold;
    private final double whiteCellThreshold;
    private final double minCellSize;

    public ProcessingParameters(
            double brightness,
            double saturation,
            double hue,
            double red,
            double green,
            double blue,
            double redCellThreshold,
            double whiteCellThreshold,
            double minCellSize) {

        this.brightness = brightness;
        this.saturation = saturation;
        this.hue = hue;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.redCellThreshold = redCellThreshold;
        this.whiteCellThreshold = whiteCellThreshold;
        this.minCellSize = minCellSize;
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
                0.0   // minCellSize
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
                0.0   // minCellSize
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
                0.0   // minCellSize
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
                0.2,  // redCellThreshold // 0.2 = More sensitive detection of red cells
                0.5,  // whiteCellThreshold // 0.5 = Moderate sensitivity for white cells
                10.0  // minCellSize

/*
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


        );
    }

    public static ProcessingParameters getDefaultTricolourBlood() {double  redCellThreshold = 0.2; // Example threshold, adjust as needed
        double whiteCellThreshold = 0.5; // Example threshold, adjust as needed
        double minCellSize = 10; // Example minimum cell size, adjust as needed
        double maxCellSize = 100; // Example maximum cell size, adjust as needed

        return new ProcessingParameters(
                0.0,  // brightness
                0.0,  // saturation
                0.0,  // hue
                0.0,  // red
                0.0,  // green
                0.0,  // blue
                redCellThreshold,
                whiteCellThreshold,
                minCellSize
        );
    }

    // Getters
    public double getMinCellSize() {
        return minCellSize;
    }
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


}

