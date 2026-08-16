package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SensorMapperTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final SensorDto SENSOR_DTO = new SensorDto(NAME, SensorType.TEMPERATURE);
    private static final SensorDto SENSOR_DTO_WITH_ID = new SensorDto(ID, NAME, SensorType.TEMPERATURE);

    private final SensorMapper underTest = new SensorMapper();

    @Test
    void testMapSensorDTOToEntityShouldWork() {
        // Given
        Sensor expectedSensor = new Sensor();
        expectedSensor.setName(NAME);
        expectedSensor.setType(SensorType.TEMPERATURE);

        // When
        Sensor result = underTest.mapSensorDTOToEntity(SENSOR_DTO);

        // Then
        assertThat(result).isEqualTo(expectedSensor);
    }

    @Test
    void testMapSensorEntityToDTOShouldWork() {
        // Given
        Sensor sensor = new Sensor();
        sensor.setId(ID);
        sensor.setName(NAME);
        sensor.setType(SensorType.TEMPERATURE);

        // When
        SensorDto result = underTest.mapSensorEntityToDTO(sensor);

        // Then
        assertThat(result).isEqualTo(SENSOR_DTO_WITH_ID);
    }
}