package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlertMapperTest {

    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    private final AlertMapper underTest = new AlertMapper();

    @Test
    void testMapAlertEntityToDTOShouldFailOnAlertWithNoSensor() {
        // Given
        Alert alert = new Alert();
        alert.setSensor(null);

        // When-Then
        assertThatThrownBy(() -> underTest.mapAlertEntityToDTO(alert))
            .isInstanceOf(SensorNotFoundException.class);
    }

    @Test
    void testMapAlertEntityToDTOShouldMapAlert() {
        // Given
        UUID sensorId = UUID.randomUUID();
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        Alert alert = new Alert();
        alert.setSensor(sensor);
        alert.setMessage(MESSAGE);
        alert.setTimestamp(TIMESTAMP);
        AlertDto expectedResult = new AlertDto(sensorId, MESSAGE, TIMESTAMP);

        // When
        AlertDto result = underTest.mapAlertEntityToDTO(alert);

        // Then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testMapAlertDTOToEntityShouldMapAlert() {
        // Given
        Sensor sensor = new Sensor();
        AlertDto alertDto = new AlertDto(UUID.randomUUID(), MESSAGE, TIMESTAMP);

        // When
        Alert result = underTest.mapAlertDTOToEntity(alertDto, sensor);

        // Then
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getTimestamp()).isEqualTo(TIMESTAMP);
        assertThat(result.getSensor()).isSameAs(sensor);
    }
}