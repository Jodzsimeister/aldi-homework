package com.aldisued.iot.monitoring.messaging;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.dto.validator.DtoValidator;
import com.aldisued.iot.monitoring.service.SensorReadingService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SensorReadingKafkaListenerTest {

    private static final SensorReadingDto SENSOR_READING_DTO = new SensorReadingDto(UUID.randomUUID(), 0.0,
        LocalDateTime.now());

    @Mock
    private SensorReadingService sensorReadingService;
    @Mock
    private DtoValidator dtoValidator;

    @InjectMocks
    private SensorReadingKafkaListener underTest;

    @Test
    void testListenShouldNotCallSensorReadingServiceWhenValidationFails() {
        // Given
        doThrow(ConstraintViolationException.class).when(dtoValidator).validateDto(SENSOR_READING_DTO);

        // When
        underTest.listen(SENSOR_READING_DTO);

        // Then
        verify(dtoValidator).validateDto(SENSOR_READING_DTO);
        verifyNoMoreInteractions(dtoValidator);
        verifyNoInteractions(sensorReadingService);
    }

    @Test
    void testListenShouldCallSensorReadingService() {
        // Given

        // When
        underTest.listen(SENSOR_READING_DTO);

        // Then
        verify(dtoValidator).validateDto(SENSOR_READING_DTO);
        verify(sensorReadingService).saveSensorReading(SENSOR_READING_DTO);
        verifyNoMoreInteractions(dtoValidator, sensorReadingService);
    }
}