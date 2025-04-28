package com.michaelmckibbin.imageanalysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingMetricsTest {
    private ProcessingMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new ProcessingMetrics();
    }

    @Test
    void testAddMetrics() {
        metrics.addMetrics(10.0, 15.0, 30.0, 100.0);

        assertEquals(1, metrics.getWhiteCellTimes().size());
        assertEquals(10.0, metrics.getWhiteCellTimes().get(0));
    }

    @Test
    void testAverages() {
        metrics.addMetrics(10.0, 15.0, 30.0, 100.0);
        metrics.addMetrics(20.0, 25.0, 50.0, 200.0);

        assertEquals(15.0, metrics.getAverageWhiteCellTime());
        assertEquals(20.0, metrics.getAverageRedCellTime());
    }

    @Test
    void testReset() {
        metrics.addMetrics(10.0, 15.0, 30.0, 100.0);
        metrics.reset();

        assertTrue(metrics.getWhiteCellTimes().isEmpty());
        assertTrue(metrics.getRedCellTimes().isEmpty());
    }
}
