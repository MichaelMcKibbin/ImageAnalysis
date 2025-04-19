package com.michaelmckibbin.imageanalysis;

public class ImageProcessorFactory {
    public static ImageProcessor createProcessor(String type) {
        return switch (type.toLowerCase()) {
            case "bw" -> new BlackAndWhiteProcessor();
            //case "objects" -> new BloodCellProcessor();
            case "tricolour" -> new TricolourBloodProcessor();
            case "union" -> new UnionFindBloodCellProcessor();
            case "union2" -> new UnionFindBlood2();  // Width and height will be set when processing
            default -> throw new IllegalArgumentException("Unknown processor type");
        };
    }
}

