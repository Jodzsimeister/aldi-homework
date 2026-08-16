package com.aldisued.iot.monitoring.messaging;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.dto.validator.DtoValidator;
import com.aldisued.iot.monitoring.service.SensorReadingService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SensorReadingKafkaListener {
  private final SensorReadingService sensorReadingService;

  private final DtoValidator dtoValidator;

  public SensorReadingKafkaListener(SensorReadingService sensorReadingService, DtoValidator dtoValidator) {
    this.sensorReadingService = sensorReadingService;
    this.dtoValidator = dtoValidator;
  }

  @KafkaListener(topics = {"sensor-reading"}, groupId = "iot-monitoring")
  public void listen(SensorReadingDto sensorReadingDto) {
    try {
      dtoValidator.validateDto(sensorReadingDto);
      sensorReadingService.saveSensorReading(sensorReadingDto);
    } catch (ConstraintViolationException exception) {
      // Log, monitor, maybe publish to error topic
    }
  }

}
