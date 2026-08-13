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
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertService {

  private static final String ALERTS_TOPIC = "alerts";

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;

  public AlertService(AlertRepository alertRepository, SensorRepository sensorRepository,
      KafkaTemplate<String, AlertDto> kafkaTemplate) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Transactional
  public AlertDto saveAlert(AlertDto alertDto) {
    Objects.requireNonNull(alertDto, "alertDto cannot be null");

    Sensor sensor = sensorRepository.findById(alertDto.sensorId())
        .orElseThrow(SensorNotFoundException::new);

    Alert alert = mapAlertDTOToEntity(alertDto, sensor);

    alertRepository.save(alert);

    AlertDto resultDTO = mapAlertEntityToDTO(alert);

    // This can be moved out of the transaction boundary and executed in a transaction synchronization hook, if the
    // publishing to the alerts topic is not mandatory for the saveAlert use-case.
    kafkaTemplate.send(ALERTS_TOPIC, resultDTO);

    return resultDTO;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    Objects.requireNonNull(sensorId, "sensorId cannot be null");

    return alertRepository.findFirstBySensorIdOrderByTimestampDesc(sensorId)
        .map(this::mapAlertEntityToDTO)
        .orElseThrow(NoAlertsForSensorException::new);
  }

  private AlertDto mapAlertEntityToDTO(Alert alert) {
    UUID sensorId = Optional.of(alert)
        .map(Alert::getSensor)
        .map(Sensor::getId)
        .orElseThrow(SensorNotFoundException::new);
    return new AlertDto(sensorId, alert.getMessage(), alert.getTimestamp());
  }

  private Alert mapAlertDTOToEntity(AlertDto alertDto, Sensor sensor) {
    Alert alert = new Alert();

    alert.setMessage(alertDto.message());
    alert.setTimestamp(alertDto.timestamp());
    alert.setSensor(sensor);

    return alert;
  }
}
