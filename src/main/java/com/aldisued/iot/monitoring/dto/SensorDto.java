package com.aldisued.iot.monitoring.dto;

import com.aldisued.iot.monitoring.entity.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SensorDto(
    UUID id,
    @NotBlank(message = "The sensor name must not be blank.") String name,
    @NotNull(message = "The sensor type must be provided.") SensorType type
) {
    public SensorDto(String name, SensorType type) {
        this(null, name, type);
    }
}
