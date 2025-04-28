package com.michaelmckibbin.imageanalysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ProcessingParametersTest {
    private ProcessingParameters params;

    @BeforeAll
    public static void setupHeadless() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }
    @BeforeEach
    void setUp() {
        params = new ProcessingParameters();
    }

    @Test
    void testDefaultValues() {
        assertEquals(0, params.getMinCellSize());
        assertEquals(0.0, params.getWhiteCellThreshold());
        assertEquals(0.0, params.getRedCellThreshold());
    }

    @Test
    void testSetValidParameters() {
        params.setMinCellSize(75);
        assertEquals(75, params.getMinCellSize());

        params.setWhiteCellThreshold(80);
        assertEquals(80, params.getWhiteCellThreshold());
    }

    @Test
    void testInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            params.setMinCellSize(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            params.setWhiteCellThreshold(101);
        });
    }
}
