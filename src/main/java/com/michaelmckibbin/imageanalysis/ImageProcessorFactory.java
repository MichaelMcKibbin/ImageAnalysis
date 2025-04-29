package com.michaelmckibbin.imageanalysis;

/**
 * Factory class to create ImageProcessor instances based on the specified type.
 * This class provides a centralized way to create different types of ImageProcessor
 * without exposing the creation logic to the client code.
 *
 * @author Michael McKibbin (20092733)
 * @version 1.0 (2024-02-20)
 *
 */
public class ImageProcessorFactory {
    public static ImageProcessor createProcessor(String type) {
        return switch (type.toLowerCase()) {
            case "bw" -> new BlackAndWhiteProcessor();
            //case "objects" -> new BloodCellProcessor();
            case "tricolour" -> new TricolourBloodProcessor();
            case "union" -> new UnionFindBloodCellProcessor();
            case "union2" -> new ConnectedComponentsProcessor();  // Width and height will be set when processing
            default -> throw new IllegalArgumentException("Unknown processor type");
        };
    }
}

