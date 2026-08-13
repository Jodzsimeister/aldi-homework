package com.aldisued.iot.monitoring.repository;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, String> {
    List<SensorReading> findAllBySensorTypeAndTimestampBetween(SensorType sensorType, LocalDateTime start,
        LocalDateTime end);
}
