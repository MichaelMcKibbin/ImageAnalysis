package com.michaelmckibbin.imageanalysis;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/** The interface defines the contract that all image processors must follow
 *
 *
 * @author Michael McKibbin (20092733)
 * @version 1.0 (2024-02-20)
 *
 */
public interface ImageProcessor {
    Image processImage(Image originalImage, ProcessingParameters params);
    Image processImage(Image originalImage);

    String getProcessorName();
}

