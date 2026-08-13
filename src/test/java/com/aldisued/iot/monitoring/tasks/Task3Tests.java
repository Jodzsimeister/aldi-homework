package com.aldisued.iot.monitoring.tasks;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.service.SensorReadingService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "/sql/task-3-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class Task3Tests extends IntegrationTestBase {

  private static final UUID SENSOR_ID = UUID.fromString(
      "e3242ea2-0514-46d3-aad8-b2012980c41c");

  @Autowired
  private SensorReadingService sensorReadingService;

  @Autowired
  private SensorReadingRepository sensorReadingRepository;

  @AfterEach
  public void cleanup() {
    sensorReadingRepository.deleteAll();
  }

  @Test()
  public void verifySensorReadingSaveFailsWhenSensorNotFound() {
    var sensorReadingDto = testSensorReadingDto(UUID.randomUUID());

    Assertions.assertThrows(SensorNotFoundException.class,
            () -> sensorReadingService.saveSensorReading(sensorReadingDto));
  }

  @Test
  @Transactional
  public void verifySensorReadingProperties() {
    var sensorReadingDto = testSensorReadingDto();

    var savedSensorReading = sensorReadingService.saveSensorReading(sensorReadingDto);

    Assertions.assertEquals(sensorReadingDto, savedSensorReading);
  }

  private static @NotNull SensorReadingDto testSensorReadingDto() {
    return testSensorReadingDto(SENSOR_ID);
  }

  private static @NotNull SensorReadingDto testSensorReadingDto(UUID sensorId) {
    return new SensorReadingDto(
        sensorId,
        23.45,
        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
    );
  }


}
