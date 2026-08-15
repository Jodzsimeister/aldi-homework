package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.SensorNameConflictException;
import com.aldisued.iot.monitoring.mapper.SensorMapper;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SensorService {

  private final SensorRepository sensorRepository;

  private final SensorMapper sensorMapper;

  public SensorService(SensorRepository sensorRepository, SensorMapper sensorMapper) {
    this.sensorRepository = sensorRepository;
    this.sensorMapper = sensorMapper;
  }

  public SensorDto saveSensor(SensorDto sensor) {
      Objects.requireNonNull(sensor, "sensor cannot be null");

      try {
          return sensorMapper.mapSensorEntityToDTO(
              sensorRepository.save(
                  sensorMapper.mapSensorDTOToEntity(sensor)));
      } catch (DataIntegrityViolationException exception) {
          if (exception.getCause() != null
                  && exception.getCause() instanceof ConstraintViolationException constraintViolationException
                  && Sensor.SENSOR_NAME_UNIQUE_CONSTRAINT_NAME.equals(
                      constraintViolationException.getConstraintName())) {
              throw new SensorNameConflictException();
          }
          throw exception;
      }
  }
}
