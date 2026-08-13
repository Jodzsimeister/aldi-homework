package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.NoAlertsForSensorException;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;

  public AlertService(AlertRepository alertRepository, SensorRepository sensorRepository,
      KafkaTemplate<String, AlertDto> kafkaTemplate) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public Alert saveAlert(AlertDto alertDto) {
    // TODO: Task 6
    return null;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    Objects.requireNonNull(sensorId, "sensorId cannot be null");

    return alertRepository.findFirstBySensorIdOrderByTimestampDesc(sensorId)
        .map(this::mapDTO)
        .orElseThrow(NoAlertsForSensorException::new);
  }

  private AlertDto mapDTO(Alert alert) {
    UUID sensorId = Optional.of(alert)
        .map(Alert::getSensor)
        .map(Sensor::getId)
        .orElseThrow(SensorNotFoundException::new);
    return new AlertDto(sensorId, alert.getMessage(), alert.getTimestamp());
  }
}
