package com.aldisued.iot.monitoring.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(
    name = "sensor_readings"
    // This composite index could be useful, but this entity seems to be write-heavy, therefore a huge amount of writes
    // would be slowed due to index update, and a few reads would be fastened via
    // MeasurementService#getMeasurementValuesBySensorType
    /*indexes = {
        @Index(
            name = "idx_sensor_type_timestamp",
            columnList = "sensor_type, timestamp"
        )
    }*/)
@Entity
public class SensorReading {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Double value;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sensor_id")
  private Sensor sensor;

  public SensorReading() {
  }

  public SensorReading(
      Double value,
      LocalDateTime timestamp,
      Sensor sensor
  ) {
    this.value = value;
    this.timestamp = timestamp;
    this.sensor = sensor;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Sensor getSensor() {
    return this.sensor;
  }

  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }
}
