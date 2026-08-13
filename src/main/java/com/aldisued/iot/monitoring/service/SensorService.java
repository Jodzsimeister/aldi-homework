package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.SensorNameConflictException;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

  private static final String SENSOR_NAME_UNIQUE_CONSTRAINT_NAME = "sensor_name_unique";

  private final SensorRepository sensorRepository;

  public SensorService(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  public SensorDto saveSensor(SensorDto sensor) {
      try {
          return mapSensorEntityToDTO(sensorRepository.save(mapSensorDTOToEntity(sensor)));
      } catch (DataIntegrityViolationException exception) {
          if (exception.getCause() != null
                  && exception.getCause() instanceof ConstraintViolationException constraintViolationException
                  && SENSOR_NAME_UNIQUE_CONSTRAINT_NAME.equals(constraintViolationException.getConstraintName())) {
              throw new SensorNameConflictException();
          }
          throw exception;
      }
  }

    private Sensor mapSensorDTOToEntity(SensorDto sensorDto) {
      return new Sensor(sensorDto.name(), sensorDto.type());
    }

    private SensorDto mapSensorEntityToDTO(Sensor sensor) {
      return new SensorDto(sensor.getName(), sensor.getType());
    }
}
