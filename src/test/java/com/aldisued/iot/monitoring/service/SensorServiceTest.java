package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.exception.SensorNameConflictException;
import com.aldisued.iot.monitoring.mapper.SensorMapper;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    private static final String NAME = "name";
    private static final Sensor SENSOR = new Sensor();
    private static final Sensor SAVED_SENSOR = new Sensor();
    private static final SensorDto SENSOR_DTO = new SensorDto(NAME, SensorType.TEMPERATURE);
    private static final SensorDto SAVED_SENSOR_DTO = new SensorDto(NAME, SensorType.TEMPERATURE);

    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private SensorMapper sensorMapper;

    @InjectMocks
    private SensorService underTest;

    @Test
    void testSaveSensorShouldThrowSensorNameConflictExceptionWhenSensorNameAlreadyExists() {
        // Given
        when(sensorMapper.mapSensorDTOToEntity(same(SENSOR_DTO))).thenReturn(SENSOR);
        doThrow(
                createDataIntegrityViolationExceptionForUniqueConstraintViolation(
                    Sensor.SENSOR_NAME_UNIQUE_CONSTRAINT_NAME))
            .when(sensorRepository)
            .saveAndFlush(same(SENSOR));

        // When-Then
        assertThatThrownBy(() -> underTest.saveSensor(SENSOR_DTO))
            .isInstanceOf(SensorNameConflictException.class);

        verify(sensorMapper).mapSensorDTOToEntity(same(SENSOR_DTO));
        verify(sensorRepository).saveAndFlush(same(SENSOR));
        verifyNoMoreInteractions(sensorMapper, sensorRepository);
    }

    @Test
    void testSaveSensorShouldReThrowCaughtDataIntegrityViolationException() {
        // Given
        when(sensorMapper.mapSensorDTOToEntity(same(SENSOR_DTO))).thenReturn(SENSOR);
        DataIntegrityViolationException dataIntegrityViolationException =
                createDataIntegrityViolationExceptionForUniqueConstraintViolation(
                        "anyOtherUniqueConstraint");
        doThrow(dataIntegrityViolationException)
            .when(sensorRepository)
            .saveAndFlush(same(SENSOR));

        // When-Then
        try {
            underTest.saveSensor(SENSOR_DTO);
            fail("A DataIntegrityViolationException should have been thrown.");
        } catch (DataIntegrityViolationException exception) {
            verify(sensorMapper).mapSensorDTOToEntity(same(SENSOR_DTO));
            verify(sensorRepository).saveAndFlush(same(SENSOR));
            verifyNoMoreInteractions(sensorMapper, sensorRepository);

            assertThat(dataIntegrityViolationException).isSameAs(exception);
        }
    }

    @Test
    void testSaveSensorShouldWork() {
        // Given
        when(sensorMapper.mapSensorDTOToEntity(same(SENSOR_DTO))).thenReturn(SENSOR);
        when(sensorRepository.saveAndFlush(same(SENSOR))).thenReturn(SAVED_SENSOR);
        when(sensorMapper.mapSensorEntityToDTO(same(SAVED_SENSOR))).thenReturn(SAVED_SENSOR_DTO);

        // When
        SensorDto result = underTest.saveSensor(SENSOR_DTO);

        // Then
        verify(sensorMapper).mapSensorDTOToEntity(same(SENSOR_DTO));
        verify(sensorRepository).saveAndFlush(same(SENSOR));
        verify(sensorMapper).mapSensorEntityToDTO(same(SAVED_SENSOR));
        verifyNoMoreInteractions(sensorMapper, sensorRepository);

        assertThat(result).isSameAs(SAVED_SENSOR_DTO);
    }

    private DataIntegrityViolationException createDataIntegrityViolationExceptionForUniqueConstraintViolation(
            String constraintName) {
        return new DataIntegrityViolationException("", new ConstraintViolationException(null, null, constraintName));
    }
}