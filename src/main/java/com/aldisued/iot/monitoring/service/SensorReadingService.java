package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.mapper.SensorReadingMapper;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class SensorReadingService {

  private final SensorReadingRepository sensorReadingRepository;
  private final SensorRepository sensorRepository;

  private final SensorReadingMapper sensorReadingMapper;

  public SensorReadingService(SensorReadingRepository sensorReadingRepository,
      SensorRepository sensorRepository, SensorReadingMapper sensorReadingMapper) {
    this.sensorReadingRepository = sensorReadingRepository;
    this.sensorRepository = sensorRepository;
    this.sensorReadingMapper = sensorReadingMapper;
  }

  @Transactional
  public SensorReadingDto saveSensorReading(SensorReadingDto sensorReadingDto) {
    Objects.requireNonNull(sensorReadingDto, "sensorReadingDto cannot be null");

    Sensor sensor = sensorRepository.findById(sensorReadingDto.sensorId())
            .orElseThrow(SensorNotFoundException::new);

    SensorReading sensorReading = sensorReadingMapper.mapSensorReadingDTOToEntity(sensorReadingDto, sensor);

    sensorReadingRepository.save(sensorReading);

    return sensorReadingMapper.mapSensorReadingEntityToDTO(sensorReading);
  }
}
