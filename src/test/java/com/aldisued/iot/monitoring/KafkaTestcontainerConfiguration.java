package com.aldisued.iot.monitoring;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Profile("docker")
@TestConfiguration(proxyBeanMethods = false)
class KafkaTestcontainerConfiguration {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:4.0.0"));
	}

	@Bean
	NewTopic sensorReadingTopic() {
		return TopicBuilder.name("sensor-reading")
			.partitions(1)
			.replicas(1)
			.build();
	}

	@Bean
	NewTopic sensorAlertsTopic() {
		return TopicBuilder.name("sensor-alerts")
			.partitions(1)
			.replicas(1)
			.build();
	}

	@Bean
	NewTopic alertsTopic() {
		return TopicBuilder.name("alerts")
			.partitions(1)
			.replicas(1)
			.build();
	}
}
