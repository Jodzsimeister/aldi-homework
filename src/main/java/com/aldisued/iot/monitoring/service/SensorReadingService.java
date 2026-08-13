package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class SensorReadingService {

  private final SensorReadingRepository sensorReadingRepository;
  private final SensorRepository sensorRepository;

  public SensorReadingService(SensorReadingRepository sensorReadingRepository,
      SensorRepository sensorRepository) {
    this.sensorReadingRepository = sensorReadingRepository;
    this.sensorRepository = sensorRepository;
  }

  @Transactional
  public SensorReadingDto saveSensorReading(SensorReadingDto sensorReadingDto) {
    Sensor sensor = sensorRepository.findById(sensorReadingDto.sensorId())
            .orElseThrow(SensorNotFoundException::new);

    SensorReading sensorReading = mapSensorReadingDTOToEntity(sensorReadingDto, sensor);
    sensorReadingRepository.save(sensorReading);

    return mapSensorReadingEntityToDTO(sensorReading);
  }

  private SensorReading mapSensorReadingDTOToEntity(SensorReadingDto sensorReadingDto, Sensor sensor) {
    return new SensorReading(
        sensorReadingDto.value(),
        sensorReadingDto.timestamp(),
        sensor);
  }

  private SensorReadingDto mapSensorReadingEntityToDTO(SensorReading sensorReading) {
    UUID sensorId = Optional.of(sensorReading)
            .map(SensorReading::getSensor)
            .map(Sensor::getId)
            .orElseThrow(SensorNotFoundException::new);
    return new SensorReadingDto(sensorId, sensorReading.getValue(), sensorReading.getTimestamp());
  }

}
