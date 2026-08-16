package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeasurementService {

  private final SensorReadingRepository sensorReadingRepository;

  public MeasurementService(SensorReadingRepository sensorReadingRepository) {
    this.sensorReadingRepository = sensorReadingRepository;
  }

  @Transactional(readOnly = true)
  public List<Double> getMeasurementValuesBySensorType(SensorType sensorType, LocalDateTime from,
      LocalDateTime to) {
    Objects.requireNonNull(sensorType, "sensorType must not be null");
    Objects.requireNonNull(from, "from must not be null");
    Objects.requireNonNull(to, "to must not be null");

    return sensorReadingRepository.findAllBySensorTypeAndTimestampBetween(sensorType, from, to,
            Sort.by("timestamp")).stream()
        .map(SensorReading::getValue)
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<Double> getAverageTemperature(LocalDateTime from, LocalDateTime to) {
    Objects.requireNonNull(from, "from must not be null");
    Objects.requireNonNull(to, "to must not be null");
    return sensorReadingRepository.findAllBySensorTypeAndTimestampBetween(SensorType.TEMPERATURE, from, to,
            Sort.unsorted()).stream()
        .mapToDouble(SensorReading::getValue)
        .average()
        .stream()
        .boxed()
        .findFirst();
  }

}
