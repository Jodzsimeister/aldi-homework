package com.aldisued.iot.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertDto(
    @NotNull(message = "The sensor id must not be null.") UUID sensorId,
    @NotBlank(message = "The message must not be blank.") String message,
    @NotNull(message = "The timestamp must not be null.") LocalDateTime timestamp
) {
}
