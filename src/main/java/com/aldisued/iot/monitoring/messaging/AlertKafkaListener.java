package com.aldisued.iot.monitoring.messaging;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.dto.validator.DtoValidator;
import com.aldisued.iot.monitoring.service.AlertService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertKafkaListener {
  private final AlertService alertService;

  private final DtoValidator dtoValidator;

  public AlertKafkaListener(AlertService alertService, DtoValidator dtoValidator) {
    this.alertService = alertService;
    this.dtoValidator = dtoValidator;
  }

  @KafkaListener(topics = {"sensor-alerts"}, groupId = "iot-monitoring")
  public void listen(AlertDto alertDto) {
    try {
      dtoValidator.validateDto(alertDto);
      alertService.saveAlert(alertDto);
    } catch (ConstraintViolationException exception) {
      // Log, monitor, maybe publish to error topic
    }
  }

}
