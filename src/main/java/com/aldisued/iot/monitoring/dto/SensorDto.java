package com.aldisued.iot.monitoring.dto;

import com.aldisued.iot.monitoring.entity.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorDto(
    @NotBlank(message = "The sensor name must not be blank.") String name,
    @NotNull(message = "The sensor type must be provided.") SensorType type
) {
}
