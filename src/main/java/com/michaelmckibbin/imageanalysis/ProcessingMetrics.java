package com.michaelmckibbin.imageanalysis;

import java.util.ArrayList;
import java.util.List;

public class ProcessingMetrics {
    private List<Double> whiteCellTimes = new ArrayList<>();
    private List<Double> redCellTimes = new ArrayList<>();
    private List<Double> totalTimes = new ArrayList<>();
    private List<Double> cellsPerSecond = new ArrayList<>();


    public List<Double> getWhiteCellTimes() {
        return whiteCellTimes;
    }

    public List<Double> getRedCellTimes() {
        return redCellTimes;
    }

    public List<Double> getTotalTimes() {
        return totalTimes;
    }

    public List<Double> getCellsPerSecond() {
        return cellsPerSecond;
    }

    public void addMetrics(double whiteTime, double redTime, double totalTime, double cps) {
        whiteCellTimes.add(whiteTime);
        redCellTimes.add(redTime);
        totalTimes.add(totalTime);
        cellsPerSecond.add(cps);
    }

    public void printAverages() {
        System.out.println("\nAverage Performance Metrics:");
        System.out.println("--------------------------");
        System.out.printf("Avg White Cell Detection: %.2f ms%n",
            whiteCellTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        System.out.printf("Avg Red Cell Detection: %.2f ms%n",
            redCellTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        System.out.printf("Avg Total Processing Time: %.2f ms%n",
            totalTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        System.out.printf("Avg Cells/Second: %.1f%n",
            cellsPerSecond.stream().mapToDouble(Double::doubleValue).average().orElse(0));
    }

    public double getAverageWhiteCellTime() {
        if (whiteCellTimes.isEmpty()) {
            return 0.0;
        }
        return whiteCellTimes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

    }

    public double getAverageRedCellTime() {
        if (redCellTimes.isEmpty()) {
            return 0.0;
        }
        return redCellTimes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }


    public void reset() {
        whiteCellTimes.clear();
        redCellTimes.clear();
        totalTimes.clear();
        cellsPerSecond.clear();
    }
}

