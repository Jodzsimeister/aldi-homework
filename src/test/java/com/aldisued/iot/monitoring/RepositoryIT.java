package com.aldisued.iot.monitoring;

import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(PostgresTestcontainerConfiguration.class)
@ActiveProfiles("docker")
public class RepositoryIT {
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private SensorReadingRepository sensorReadingRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void testRepositoryMethods() {
        Sensor sensor = new Sensor();
        sensor.setName("sensor1");
        sensor.setType(SensorType.TEMPERATURE);

        sensorRepository.saveAndFlush(sensor);

        Alert alert = new Alert();
        alert.setSensor(sensor);
        alert.setMessage("alert1");
        alert.setTimestamp(LocalDateTime.parse("2020-01-02T00:00:00"));

        alertRepository.saveAndFlush(alert);

        SensorReading reading = new SensorReading();
        reading.setSensor(sensor);
        reading.setValue(5.0);
        reading.setTimestamp(LocalDateTime.parse("2020-01-01T00:00:00"));

        sensorReadingRepository.saveAndFlush(reading);

        List<Sensor> sensors = sensorRepository.findAll();
        List<Alert> alerts = alertRepository.findAll();
        List<SensorReading> readings = sensorReadingRepository.findAll();

        assertThat(sensors).hasSize(1);
        assertThat(sensors.getFirst()).isEqualTo(sensor);
        assertThat(alerts).hasSize(1);
        assertThat(alerts.getFirst()).isEqualTo(alert);
        assertThat(readings).hasSize(1);
        assertThat(readings.getFirst()).isEqualTo(reading);

        sensor.setName("sensor1Updated");

        sensorRepository.saveAndFlush(sensor);

        entityManager.clear();

        sensor = sensorRepository.findById(sensor.getId()).get();

        assertThat(sensor.getName()).isEqualTo("sensor1Updated");
        assertThat(sensor.getAlerts()).hasSize(1);
        assertThat(sensor.getSensorReadings()).hasSize(1);

        alert.setMessage("alert1Updated");
        reading.setValue(6.0);

        alertRepository.saveAndFlush(alert);
        sensorReadingRepository.saveAndFlush(reading);

        entityManager.clear();

        alert = alertRepository.findById(alert.getId()).get();
        reading = sensorReadingRepository.findById(reading.getId()).get();

        assertThat(alert.getMessage()).isEqualTo("alert1Updated");
        assertThat(reading.getValue()).isEqualTo(6.0);

        alertRepository.deleteAll();
        alertRepository.flush();
        sensorReadingRepository.deleteAll();
        sensorReadingRepository.flush();
        sensorRepository.deleteAll();
        sensorRepository.flush();

        entityManager.clear();

        assertThat(sensorRepository.count()).isZero();
        assertThat(alertRepository.count()).isZero();
        assertThat(sensorReadingRepository.count()).isZero();
    }
}
