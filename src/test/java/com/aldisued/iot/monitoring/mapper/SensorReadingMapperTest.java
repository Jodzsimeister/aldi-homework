package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SensorReadingMapperTest {

    private static final Double VALUE = 5.0;
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    private final SensorReadingMapper underTest = new  SensorReadingMapper();

    @Test
    void testMapSensorReadingEntityToDTOShouldFailOnAlertWithNoSensor() {
        // Given
        SensorReading sensorReading = new SensorReading();
        sensorReading.setSensor(null);

        // When-Then
        assertThatThrownBy(() -> underTest.mapSensorReadingEntityToDTO(sensorReading))
                .isInstanceOf(SensorNotFoundException.class);
    }

    @Test
    void testMapSensorReadingEntityToDTOShouldMapAlert() {
        // Given
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        SensorReading sensorReading = new SensorReading();
        sensorReading.setSensor(sensor);
        sensorReading.setValue(VALUE);
        sensorReading.setTimestamp(TIMESTAMP);
        SensorReadingDto expectedResult = new SensorReadingDto(sensorId, VALUE, TIMESTAMP);

        // When
        SensorReadingDto result = underTest.mapSensorReadingEntityToDTO(sensorReading);

        // Then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testMapSensorReadingDTOToEntityShouldMapAlert() {
        // Given
        Sensor sensor = new Sensor();
        SensorReadingDto sensorReadingDto = new SensorReadingDto(UUID.randomUUID(), VALUE, TIMESTAMP);

        // When
        SensorReading result = underTest.mapSensorReadingDTOToEntity(sensorReadingDto, sensor);

        // Then
        assertThat(result.getValue()).isEqualTo(VALUE);
        assertThat(result.getTimestamp()).isEqualTo(TIMESTAMP);
        assertThat(result.getSensor()).isSameAs(sensor);
    }
}