# Blood Cell Image Analysis

An application for analyzing microscope images to detect and count blood cells using computer vision techniques.

![Original Image](docs/images/Main_view.jpg "Analysis in action!")

## Overview

This JavaFX application processes microscope images of blood samples to identify and count individual blood cells. It uses image processing techniques including thresholding, connected component analysis, and size-based filtering.

## How It Works

The analysis process follows these steps:

1. **Image Pre-processing**
   - Converts the image to grayscale
   - Applies brightness adjustments
   - Uses thresholding to separate cells from background
   
   <br>

2. **Cell Detection**
   - Implements Union-Find algorithm to identify connected components
   - Filters components based on size parameters to identify individual cells
   - Marks detected cells on the image

   <br>

3. **Tricolour Analysis**
   - Classifies detected cells based on size characteristics
   - Uses color coding to visualize different cell categories:
      - Pink: Red Blood Cells
      - Purple: White Blood Cell Nuclei
   - Eliminates cell border detection errors by turning the background white.

   <br>

## Sample Images
### Original (stained) Slide Image
<figure>
  <img src="docs/images/original_image.jpg" alt="Example Input Image" title="Original microscope image of blood cells">
  <figcaption>Original microscope image showing blood cells</figcaption>
</figure>

 <br>

### Processed Image with Cells defined and counted
<figure>
  <img src="docs/images/processed_image.jpg" alt="Analysis Result" title="Blood cells detected and highlighted using image processing">
  <figcaption>Processed image with detected blood cells highlighted</figcaption>
</figure>

 <br>

### Tricolour Process
<figure>
  <img src="docs/images/processed_tricolour_image.jpg" alt="Tricolour Analysis Result" title="Eliminating background and defining Red and White Cells">
  <figcaption>Tricolour visualization showing cell classification</figcaption>
</figure>

 <br>

## Features

- Real-time parameter adjustment
- Visual feedback of detection results
- Size-based filtering to distinguish cells
- Cell count statistics

## Technical Details

- Built with JavaFX and OpenCV
- Uses Union-Find algorithm for connected component labeling
- Implements custom image processing filters
