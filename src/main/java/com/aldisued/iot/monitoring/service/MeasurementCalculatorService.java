package com.aldisued.iot.monitoring.service;


import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class MeasurementCalculatorService {

  public List<Double> filterByAverageDeviation(List<Double> values, Double deviation) {
    Objects.requireNonNull(values, "values must not be null");
    Objects.requireNonNull(deviation, "deviation cannot be null");

    validateFilterByAverageDeviationInput(values, deviation);

    double average = values.stream()
        .mapToDouble(Double::doubleValue)
        .average()
        .orElse(0.0);
    double deviationValue = Math.abs(average) * deviation;
    double lowerRange = average - deviationValue;
    double upperRange = average + deviationValue;

    return values.stream()
        .filter(value -> doFilterByAverageDeviation(value, lowerRange, upperRange))
        .toList();
  }

  public List<Double> getMovingAverage(List<Double> data, int windowSize) {
    // TODO: Task 10
    return List.of();
  }

  private void validateFilterByAverageDeviationInput(List<Double> values, double deviation) {
    // Since no explicit requirement exists for null values, I decided to treat the input list as invalid, if it
    // contains any null values. In a real scenario, missing requirements like this need to be clarified before the task
    // is started.
    if (values.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("Data must not contain null values.");
    }
    if (!Double.isFinite(deviation) || deviation < 0.0 || deviation > 1.0) {
      throw new IllegalArgumentException("Deviation must be between 0.0 and 1.0.");
    }
  }

  private boolean doFilterByAverageDeviation(double value, double lowerRange, double upperRange) {
    return value >= lowerRange && value <= upperRange;
  }

}
