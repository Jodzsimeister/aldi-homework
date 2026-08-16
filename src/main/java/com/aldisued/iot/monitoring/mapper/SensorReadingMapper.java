package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SensorReadingMapper {
    public SensorReadingDto mapSensorReadingEntityToDTO(SensorReading sensorReading) {
        UUID sensorId = Optional.of(sensorReading)
                .map(SensorReading::getSensor)
                .map(Sensor::getId)
                .orElseThrow(SensorNotFoundException::new);
        return new SensorReadingDto(sensorId, sensorReading.getValue(), sensorReading.getTimestamp());
    }

    public SensorReading mapSensorReadingDTOToEntity(SensorReadingDto sensorReadingDto, Sensor sensor) {
        return new SensorReading(
                sensorReadingDto.value(),
                sensorReadingDto.timestamp(),
                sensor);
    }
}
