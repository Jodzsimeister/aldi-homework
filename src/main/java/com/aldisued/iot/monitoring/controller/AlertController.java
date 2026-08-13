package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/alerts")
public class AlertController {

  private final AlertService alertService;

  public AlertController(AlertService alertService) {
    this.alertService = alertService;
  }

  @GetMapping("/latest")
  public AlertDto getLatestAlerts(@RequestParam(name = "sensorId") UUID sensorId) {
    return alertService.findLastAlertBySensorId(sensorId);
  }

  @PostMapping
  public AlertDto saveAlert(@RequestBody @Valid AlertDto alertDto) {
    return alertService.saveAlert(alertDto);
  }
}
