package com.aldisued.iot.monitoring.repository;

import com.aldisued.iot.monitoring.entity.Alert;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, String> {
    @EntityGraph(attributePaths = "sensor")
    Optional<Alert> findFirstBySensorIdOrderByTimestampDesc(UUID sensorId);
}
