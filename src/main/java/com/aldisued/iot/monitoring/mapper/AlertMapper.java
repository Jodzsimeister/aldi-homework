package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AlertMapper {
    public AlertDto mapAlertEntityToDTO(Alert alert) {
        UUID sensorId = Optional.of(alert)
            .map(Alert::getSensor)
            .map(Sensor::getId)
            .orElseThrow(SensorNotFoundException::new);
        return new AlertDto(sensorId, alert.getMessage(), alert.getTimestamp());
    }

    public Alert mapAlertDTOToEntity(AlertDto alertDto, Sensor sensor) {
        Alert alert = new Alert();

        alert.setMessage(alertDto.message());
        alert.setTimestamp(alertDto.timestamp());
        alert.setSensor(sensor);

        return alert;
    }
}
