package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.NoAlertsForSensorException;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.mapper.AlertMapper;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    private static final UUID SENSOR_ID = UUID.randomUUID();
    private static final AlertDto ALERT_DTO = new AlertDto(SENSOR_ID, null, null);
    private static final Sensor SENSOR = new Sensor();
    private static final Alert ALERT = new Alert();
    private static final AlertDto SAVED_ALERT_DTO = new AlertDto(SENSOR_ID, null, null);

    @Mock
    private AlertRepository alertRepository;
    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private AlertMapper alertMapper;
    @Mock
    private KafkaTemplate<String, AlertDto> kafkaTemplate;

    @InjectMocks
    private AlertService underTest;

    @Test
    void testSaveSensorReadingShouldThrowSensorNotFoundExceptionWhenSensorNotFound() {
        // Given
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.empty());

        // When-Then
        assertThatThrownBy(() -> underTest.saveAlert(ALERT_DTO))
                .isInstanceOf(SensorNotFoundException.class);
        verify(sensorRepository).findById(SENSOR_ID);
        verifyNoInteractions(alertRepository, alertMapper, kafkaTemplate);
        verifyNoMoreInteractions(sensorRepository);
    }

    @Test
    void testSaveSensorReadingShouldWork() {
        // Given
        when(sensorRepository.findById(SENSOR_ID)).thenReturn(Optional.of(SENSOR));
        when(alertMapper.mapAlertDTOToEntity(same(ALERT_DTO), same(SENSOR))).thenReturn(ALERT);
        when(alertRepository.save(ALERT)).thenReturn(ALERT);
        when(alertMapper.mapAlertEntityToDTO(same(ALERT))).thenReturn(SAVED_ALERT_DTO);

        // When
        AlertDto result = underTest.saveAlert(ALERT_DTO);

        // Then
        verify(sensorRepository).findById(SENSOR_ID);
        verify(alertMapper).mapAlertDTOToEntity(same(ALERT_DTO), same(SENSOR));
        verify(alertRepository).save(same(ALERT));
        verify(alertMapper).mapAlertEntityToDTO(same(ALERT));
        verify(kafkaTemplate).send(eq(AlertService.ALERTS_TOPIC), same(SAVED_ALERT_DTO));
        verifyNoMoreInteractions(sensorRepository, alertMapper, alertRepository);

        assertThat(result).isSameAs(SAVED_ALERT_DTO);
    }

    @Test
    void testFindLastAlertBySensorIdShouldThrowNoAlertsForSensorExceptionWhenNoAlertFound() {
        // Given
        when(alertRepository.findFirstBySensorIdOrderByTimestampDesc(SENSOR_ID)).thenReturn(Optional.empty());

        // When-Then
        assertThatThrownBy(() -> underTest.findLastAlertBySensorId(SENSOR_ID))
            .isInstanceOf(NoAlertsForSensorException.class);

        verify(alertRepository).findFirstBySensorIdOrderByTimestampDesc(SENSOR_ID);
        verifyNoMoreInteractions(alertRepository);
        verifyNoInteractions(alertMapper);
    }

    @Test
    void testFindLastAlertBySensorIdShouldWork() {
        // Given
        when(alertRepository.findFirstBySensorIdOrderByTimestampDesc(SENSOR_ID)).thenReturn(Optional.of(ALERT));
        when(alertMapper.mapAlertEntityToDTO(same(ALERT))).thenReturn(ALERT_DTO);

        // When
        AlertDto result = underTest.findLastAlertBySensorId(SENSOR_ID);

        // Then
        verify(alertRepository).findFirstBySensorIdOrderByTimestampDesc(SENSOR_ID);
        verify(alertMapper).mapAlertEntityToDTO(same(ALERT));
        verifyNoMoreInteractions(alertRepository, alertMapper);

        assertThat(result).isSameAs(ALERT_DTO);
    }
}