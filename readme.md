# Blood Cell Image Analysis

 

![Original Image](docs/images/Main_view.jpg "Analysis in action!")

## Overview

This JavaFX application processes microscope images of blood samples to identify and count individual blood cells. It uses image processing techniques including thresholding, connected component analysis, and size-based filtering.

## How It Works

The analysis process includes these steps:

   - Converts the image to three colours
   - Uses thresholding to separate cells from background
   - Implements Union-Find algorithm to identify connected components
   - Differentiates Red and White Cells
   - Classifies detected cells based on size & colour
   - Uses color coding to visualize different cell categories:
      - Pink: Red Blood Cells
      - Purple: White Blood Cell Nuclei
   - Removes background colour & noise
   - Marks detected cells on the image

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
- Adjustable filtering to refine results
- Cell count statistics

## Technical Details

- Built with Java & JavaFX
- Uses a Union-Find algorithm for cell identification
- Implements custom image processing filters



## Adjusting Detection levels with Minimum Cell Size slider
<div style="display: flex; flex-wrap: wrap; gap: 20px; justify-content: center;">
  <figure style="margin: 0; text-align: center;">
    <img src="docs/images/NB1ln3_processed_20250430_010027.jpg" alt="Description 4" width="400">
  </figure>
  <figure style="margin: 0; text-align: center;">
    <img src="docs/images/NB1ln3_processed_20250430_010015.jpg" alt="Description 1" width="400">
  </figure>
  <figure style="margin: 0; text-align: center;">
    <img src="docs/images/NB1ln3_processed_20250430_005949.jpg" alt="Description 2" width="400">
  </figure>
  <figure style="margin: 0; text-align: center;">
    <img src="docs/images/NB1ln3_processed_20250430_005933.jpg" alt="Description 3" width="400">
  </figure>
</div>