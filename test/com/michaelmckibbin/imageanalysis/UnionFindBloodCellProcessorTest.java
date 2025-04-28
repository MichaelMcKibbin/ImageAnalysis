package com.michaelmckibbin.imageanalysis;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.paint.Color;

@ExtendWith(ApplicationExtension.class)
class UnionFindBloodCellProcessorTest {
    private UnionFindBloodCellProcessor processor;
    private ProcessingParameters params;

    @BeforeAll
    public static void setupClass() throws Exception {
        // Initialize JavaFX Toolkit
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        // Run setup on JavaFX thread
        Platform.runLater(() -> {
            processor = new UnionFindBloodCellProcessor();
            params = new ProcessingParameters();
            // Set default parameters
            params.setMinCellSize(50);
            params.setWhiteCellThreshold(70);
            params.setRedCellThreshold(60);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testPerformanceMetrics() {
        final boolean[] testComplete = {false};
        final boolean[] assertionsPassed = {true};

        Platform.runLater(() -> {
            try {
                WritableImage testImage = createTestImage();
                processor.processImage(testImage, params);

                // Verify metrics were collected
                assertNotNull(processor.getMetrics());
                assertTrue(processor.getMetrics().getTotalTimes().size() > 0);
            } catch (AssertionError e) {
                assertionsPassed[0] = false;
            } finally {
                testComplete[0] = true;
            }
        });

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(testComplete[0]);
        assertTrue(assertionsPassed[0], "Assertions failed in JavaFX thread");
    }

//    @Test
//    void testProcessImageWithNullImage() {
//        final boolean[] testComplete = {false};
//
//        Platform.runLater(() -> {
//            assertThrows(IllegalArgumentException.class, () -> {
//                processor.processImage(null, params);
//            });
//            testComplete[0] = true;
//        });
//
//        WaitForAsyncUtils.waitForFxEvents();
//        assertTrue(testComplete[0]);
//    }
//
//    @Test
//    void testProcessImageWithNullParameters() {
//        final boolean[] testComplete = {false};
//
//        Platform.runLater(() -> {
//            Image testImage = new WritableImage(100, 100);
//            assertThrows(IllegalArgumentException.class, () -> {
//                processor.processImage(testImage, null);
//            });
//            testComplete[0] = true;
//        });
//
//        WaitForAsyncUtils.waitForFxEvents();
//        assertTrue(testComplete[0]);
//    }

    @Test
    void testCellDetectionWithKnownImage() {
        final boolean[] testComplete = {false};
        final boolean[] assertionsPassed = {true};

        Platform.runLater(() -> {
            try {
                WritableImage testImage = createTestImage();
                Image result = processor.processImage(testImage, params);

                // Verify the result is not null
                assertNotNull(result);
                // Verify dimensions match
                assertEquals(testImage.getWidth(), result.getWidth());
                assertEquals(testImage.getHeight(), result.getHeight());
            } catch (AssertionError e) {
                assertionsPassed[0] = false;
            } finally {
                testComplete[0] = true;
            }
        });

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(testComplete[0]);
        assertTrue(assertionsPassed[0], "Assertions failed in JavaFX thread");
    }

    private WritableImage createTestImage() {
        WritableImage image = new WritableImage(100, 100);
        PixelWriter writer = image.getPixelWriter();
        // Create a simple pattern that represents cells
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if ((x + y) % 10 < 5) {
                    writer.setColor(x, y, Color.WHITE);
                } else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }
        return image;
    }
}
