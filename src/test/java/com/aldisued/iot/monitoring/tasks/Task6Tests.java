package com.aldisued.iot.monitoring.tasks;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.service.AlertService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "/sql/task-5-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class Task6Tests extends IntegrationTestBase {

  private static final UUID SENSOR_ID = UUID.fromString(
      "e3242ea2-0514-46d3-aad8-b2012980c41c");
  private static final AlertDto ALERT_DTO_WITHOUT_SENSOR_ID = new AlertDto(null, "message",
      LocalDateTime.now());
  private static final AlertDto ALERT_DTO_WITHOUT_MESSAGE = new AlertDto(SENSOR_ID, null,
      LocalDateTime.now());
  private static final AlertDto ALERT_DTO_WITHOUT_TIMESTAMP = new AlertDto(SENSOR_ID, "message",
      null);

  @Autowired
  private AlertService alertService;

  @Autowired
  private AlertRepository alertRepository;

  private static final String BASE_ENDPOINT = "/alerts";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private KafkaTemplate<String, AlertDto> kafkaTemplate;

  @AfterEach
  public void cleanup() {
    alertRepository.deleteAll();
  }

  @Test
  @Transactional
  public void verifyMissingSensorIdHandling() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post(BASE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ALERT_DTO_WITHOUT_SENSOR_ID)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void verifyMissingMessageHandling() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post(BASE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ALERT_DTO_WITHOUT_MESSAGE)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void verifyMissingTimestampHandling() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post(BASE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ALERT_DTO_WITHOUT_TIMESTAMP)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void verifySensorReadingProperties() throws Exception {
    var alertDto = testAlertDto();

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(alertDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sensorId", Matchers.equalTo(SENSOR_ID.toString())))
        .andExpect(jsonPath("$.message", Matchers.equalTo(alertDto.message())))
        .andReturn();

    String timestamp = JsonPath.read(result.getResponse().getContentAsString(), "$.timestamp");

    Assertions.assertEquals(alertDto.timestamp().truncatedTo(ChronoUnit.MILLIS),
        LocalDateTime.parse(timestamp).truncatedTo(ChronoUnit.MILLIS));
  }

  @Test
  @Transactional
  public void verifySensorReadingProperties2() {
    var alertDto = testAlertDto();

    AlertDto savedAlert = alertService.saveAlert(alertDto);

    Assertions.assertEquals(alertDto.message(), savedAlert.message());
    Assertions.assertEquals(alertDto.timestamp(), savedAlert.timestamp());
  }

  @Test
  @Transactional
  public void verifySensorEntity() {
    var alertDto = testAlertDto();

    AlertDto alert = alertService.saveAlert(alertDto);

    Assertions.assertEquals(SENSOR_ID, alert.sensorId());
  }

  @Test
  @Transactional
  public void verifyKafkaMessage() {
    var alertDto = testAlertDto();

    alertService.saveAlert(alertDto);

    Mockito.verify(
        kafkaTemplate,
        Mockito.times(1)).send(eq("alerts"), eq(alertDto)
    );
  }

  private static @NotNull AlertDto testAlertDto() {
    return new AlertDto(
        SENSOR_ID,
        "Alert message",
        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
    );
  }

}
