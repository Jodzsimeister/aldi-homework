package com.aldisued.iot.monitoring;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import com.aldisued.iot.monitoring.service.SensorService;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({PostgresTestcontainerConfiguration.class, KafkaTestcontainerConfiguration.class})
@ActiveProfiles("docker")
public class KafkaIT {

    private final SensorDto SENSOR = new SensorDto("sensor1", SensorType.TEMPERATURE);

    @Autowired
    private SensorService sensorService;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private ConsumerFactory<String, AlertDto> alertConsumerFactory;

    @Autowired
    private KafkaTemplate<String, AlertDto> alertDtoProducer;
    @Autowired
    private KafkaTemplate<String, SensorReadingDto> sensorReadingDtoProducer;

    @Autowired
    private SensorRepository sensorRepository;

    @BeforeEach
    public void init() {
        sensorRepository.deleteAll();
    }

    @Test
    void testSensorAlertConsumeAndAlertProduce() {
        SensorDto savedSensor = sensorService.saveSensor(SENSOR);
        LocalDateTime now = LocalDateTime.now();

        try (Consumer<String, AlertDto> consumer = alertConsumerFactory.createConsumer("test-alert-group",
                "test-alert-consumer")) {
            consumer.subscribe(List.of("alerts"));

            alertDtoProducer.send("sensor-alerts", new AlertDto(savedSensor.id(), "alert1",
                    now))
                .join();

            AlertDto receivedAlert = KafkaTestUtils.getSingleRecord(consumer, "alerts", Duration.ofSeconds(10))
                .value();

            assertThat(receivedAlert).isNotNull();
            assertThat(receivedAlert.sensorId()).isEqualTo(savedSensor.id());
            assertThat(receivedAlert.message()).isEqualTo("alert1");
            assertThat(receivedAlert.timestamp()).isEqualTo(now);
        }
    }

    @Test
    void testSensorReadingConsume() {
        SensorDto savedSensor = sensorService.saveSensor(SENSOR);
        LocalDateTime now = LocalDateTime.now();
        sensorReadingDtoProducer.send("sensor-reading", new SensorReadingDto(savedSensor.id(), 6.0, now))
            .join();

        await()
            .atMost(Duration.ofSeconds(1))
            .pollInterval(Duration.ofMillis(50))
            .untilAsserted(() -> {
                var result = sensorReadingRepository.findAll();

                assertThat(result)
                    .anySatisfy(reading -> {
                        assertThat(reading.getSensor().getId()).isEqualTo(savedSensor.id());
                        assertThat(reading.getValue()).isEqualTo(6.0);
                        assertThat(reading.getTimestamp().truncatedTo(ChronoUnit.MILLIS))
                            .isEqualTo(now.truncatedTo(ChronoUnit.MILLIS));
                    });
            });
    }
}
