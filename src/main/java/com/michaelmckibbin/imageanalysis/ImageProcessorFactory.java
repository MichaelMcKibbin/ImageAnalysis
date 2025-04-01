package com.michaelmckibbin.imageanalysis;

public class ImageProcessorFactory {
    public static ImageProcessor createProcessor(String type) {
        return switch (type.toLowerCase()) {
            case "bw" -> new BlackAndWhiteProcessor();
            case "grayscale" -> new GrayscaleProcessor();
            case "sepia" -> new SepiaProcessor();
            case "objects" -> new BloodCellProcessor();
            case "tricolour" -> new TricolourBloodProcessor();
            default -> throw new IllegalArgumentException("Unknown processor type");
        };
    }
}

