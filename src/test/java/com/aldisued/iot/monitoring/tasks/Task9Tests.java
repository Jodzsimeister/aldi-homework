package com.aldisued.iot.monitoring.tasks;

import com.aldisued.iot.monitoring.service.MeasurementCalculatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class Task9Tests {

  private static final List<Double> LIST_WITH_NULL_VALUES = new ArrayList<>(3);

  static {
    LIST_WITH_NULL_VALUES.add(5.0);
    LIST_WITH_NULL_VALUES.add(null);
    LIST_WITH_NULL_VALUES.add(6.0);
  }

  private final MeasurementCalculatorService measurementCalculatorService
      = new MeasurementCalculatorService();

  @ParameterizedTest
  @MethodSource("filteredValuesByAverageDeviationSource")
  public void verifyFilteredAverageDeviation(
      List<Double> values,
      Double deviation,
      List<Double> expectedValues) {
    List<Double> filteredByAverageDeviation = measurementCalculatorService.filterByAverageDeviation(
        values, deviation);

    Assertions.assertEquals(expectedValues, filteredByAverageDeviation);
  }

  @Test
  public void verifyFilteredAverageDeviationFailsOnValuesWithNull() {
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> measurementCalculatorService.filterByAverageDeviation(LIST_WITH_NULL_VALUES, 0.2));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.NaN, -0.1, 1.1, 2.0})
  public void verifyInvalidDeviationHandling(Double deviation) {
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> measurementCalculatorService.filterByAverageDeviation(Collections.emptyList(),
            deviation));
  }

  static Stream<Arguments> filteredValuesByAverageDeviationSource() {
    return Stream.of(
        Arguments.of(
            List.of(10.0, 11.0, 12.0),
            0.1,
            List.of(10.0, 11.0, 12.0)
        ),
        Arguments.of(
            List.of(10.0, 15.0, 20.0),
            0.3,
            List.of(15.0)
        ),
        Arguments.of(
            List.of(0.0, 100.0),
            0.99,
            Collections.emptyList()
        ),
        Arguments.of(
            List.of(-5.0, -15.0, -9.0, -11.0),
            0.4,
            List.of(-9.0, -11.0)
        ),
        Arguments.of(
            Collections.emptyList(),
            0.3,
            Collections.emptyList()
        )
    );
  }

}
