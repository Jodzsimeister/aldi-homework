package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {
    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @InjectMocks
    private MeasurementService underTest;

    @ParameterizedTest
    @MethodSource("measurementValuesBySensorType")
    void testGetMeasurementValuesBySensorTypeShouldWork(SensorType sensorType,
            LocalDateTime from,
            LocalDateTime to,
            List<SensorReading> sensorReadings,
            List<Double> expectedValues) {
        // Given
        when(sensorReadingRepository.findAllBySensorTypeAndTimestampBetween(sensorType, from, to,
            Sort.by("timestamp"))).thenReturn(sensorReadings);

        // When
        List<Double> result = underTest.getMeasurementValuesBySensorType(sensorType, from, to);

        // Then
        verify(sensorReadingRepository).findAllBySensorTypeAndTimestampBetween(sensorType, from, to,
            Sort.by("timestamp"));
        verifyNoMoreInteractions(sensorReadingRepository);

        assertThat(result).isEqualTo(expectedValues);
    }

    @ParameterizedTest
    @MethodSource("averageTemperatureParameters")
    void testGetAverageTemperatureShouldWork(LocalDateTime from,
            LocalDateTime to, List<SensorReading> sensorReadings, Optional<Double> expectedValue) {
        // Given
        when(sensorReadingRepository.findAllBySensorTypeAndTimestampBetween(SensorType.TEMPERATURE, from, to,
            Sort.unsorted())).thenReturn(sensorReadings);

        // When
        Optional<Double> result = underTest.getAverageTemperature(from, to);

        // Then
        verify(sensorReadingRepository).findAllBySensorTypeAndTimestampBetween(SensorType.TEMPERATURE, from, to,
            Sort.unsorted());
        verifyNoMoreInteractions(sensorReadingRepository);

        assertThat(result).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> measurementValuesBySensorType() {
        return Stream.of(
            Arguments.of(SensorType.TEMPERATURE,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"),
                List.of(
                    createSensorReading(15.8),
                    createSensorReading(16.1),
                    createSensorReading(17.3)
                ),
                List.of(15.8, 16.1, 17.3)),
            Arguments.of(SensorType.HUMIDITY,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"),
                List.of(
                    createSensorReading(0.45),
                    createSensorReading(0.44),
                    createSensorReading(0.41)
                ),
                List.of(0.45, 0.44, 0.41)),
            Arguments.of(SensorType.ATMOSPHERIC_PRESSURE,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"),
                List.of(
                    createSensorReading(98.4),
                    createSensorReading(98.2),
                    createSensorReading(95.4)
                ),
                List.of(98.4, 98.2, 95.4)),
            Arguments.of(SensorType.TEMPERATURE,
                LocalDateTime.parse("2020-06-22T00:00:00"),
                LocalDateTime.parse("2020-06-22T23:59:59"),
                List.of(
                    createSensorReading(15.8)
                ),
                List.of(15.8))
        );
    }

    public static Stream<Arguments> averageTemperatureParameters() {
        return Stream.of(
            Arguments.of(
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"),
                List.of(
                    createSensorReading(15.0),
                    createSensorReading(18.0),
                    createSensorReading(20.0),
                    createSensorReading(21.0),
                    createSensorReading(12.0),
                    createSensorReading(13.0)
                ),
                Optional.of(16.5)
            ),
            Arguments.of(
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-06-23T23:59:59"),
                List.of(
                    createSensorReading(15.0),
                    createSensorReading(18.0),
                    createSensorReading(20.0),
                    createSensorReading(21.0)
                ),
                Optional.of(18.5)
            ),
            Arguments.of(
                LocalDateTime.parse("2025-01-01T00:00:00"),
                LocalDateTime.parse("2025-12-31T23:59:59"),
                List.of(),
                Optional.empty()
            )
        );
    }

    private static SensorReading createSensorReading(Double value) {
        SensorReading sensorReading = new SensorReading();

        sensorReading.setValue(value);

        return sensorReading;
    }
}