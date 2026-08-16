package com.aldisued.iot.monitoring;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.service.SensorService;
import org.apache.kafka.clients.consumer.Consumer;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({PostgresTestcontainerConfiguration.class, KafkaTestcontainerConfiguration.class})
@ActiveProfiles("docker")
public class KafkaIT {

    private final SensorDto SENSOR = new SensorDto("sensor1", SensorType.TEMPERATURE);

    @Autowired
    private SensorService sensorService;
    @Autowired
    private ConsumerFactory<String, AlertDto> consumerFactory;

    @Autowired
    private KafkaTemplate<String, AlertDto> alertDtoProducer;

    @Test
    void test() {
        SensorDto savedSensor = sensorService.saveSensor(SENSOR);
        LocalDateTime now = LocalDateTime.now();
        alertDtoProducer.send("sensor-alerts", new AlertDto(savedSensor.id(), "alert1",
            now));

        try (Consumer<String, AlertDto> consumer = consumerFactory.createConsumer("test-alert-group",
                "test-alert-consumer")) {
            consumer.subscribe(List.of("alerts"));

            AlertDto receivedAlert = KafkaTestUtils.getSingleRecord(consumer, "alerts", Duration.ofSeconds(10))
                .value();

            assertThat(receivedAlert).isNotNull();
            assertThat(receivedAlert.sensorId()).isEqualTo(savedSensor.id());
            assertThat(receivedAlert.message()).isEqualTo("alert1");
            assertThat(receivedAlert.timestamp()).isEqualTo(now);
        }
    }
}
