package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.mapper.SensorReadingMapper;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorReadingServiceTest {

    private static final UUID SENSOR_ID = UUID.randomUUID();
    private static final SensorReadingDto SENSOR_READING_DTO = new SensorReadingDto(SENSOR_ID, null,
        null);
    private static final Sensor SENSOR = new Sensor();
    private static final SensorReading SENSOR_READING = new SensorReading();
    private static final SensorReadingDto SAVED_SENSOR_READING_DTO = new SensorReadingDto(SENSOR_ID, null,
        null);

    @Mock
    private SensorReadingRepository sensorReadingRepository;
    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private SensorReadingMapper sensorReadingMapper;

    @InjectMocks
    private SensorReadingService underTest;

    @Test
    void testSaveSensorReadingShouldThrowSensorNotFoundExceptionWhenSensorNotFound() {
        // Given
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.empty());

        // When-Then
        assertThatThrownBy(() -> underTest.saveSensorReading(SENSOR_READING_DTO))
            .isInstanceOf(SensorNotFoundException.class);
        verify(sensorRepository).findById(SENSOR_ID);
        verifyNoInteractions(sensorReadingRepository, sensorReadingMapper);
        verifyNoMoreInteractions(sensorRepository);
    }

    @Test
    void testSaveSensorReadingShouldWork() {
        // Given
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(SENSOR));
        when(sensorReadingMapper.mapSensorReadingDTOToEntity(same(SENSOR_READING_DTO), same(SENSOR)))
            .thenReturn(SENSOR_READING);
        when(sensorReadingMapper.mapSensorReadingEntityToDTO(same(SENSOR_READING)))
            .thenReturn(SAVED_SENSOR_READING_DTO);

        // When
        SensorReadingDto result = underTest.saveSensorReading(SENSOR_READING_DTO);

        // Then
        verify(sensorRepository).findById(SENSOR_ID);
        verify(sensorReadingMapper).mapSensorReadingDTOToEntity(same(SENSOR_READING_DTO), same(SENSOR));
        verify(sensorReadingRepository).save(same(SENSOR_READING));
        verify(sensorReadingMapper).mapSensorReadingEntityToDTO(same(SENSOR_READING));
        verifyNoMoreInteractions(sensorRepository, sensorReadingMapper, sensorReadingRepository);

        assertThat(result).isSameAs(SAVED_SENSOR_READING_DTO);
    }
}