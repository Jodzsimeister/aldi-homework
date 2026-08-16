package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import org.springframework.stereotype.Component;

@Component
public class SensorMapper {
    public Sensor mapSensorDTOToEntity(SensorDto sensorDto) {
        return new Sensor(sensorDto.name(), sensorDto.type());
    }

    public SensorDto mapSensorEntityToDTO(Sensor sensor) {
        return new SensorDto(sensor.getId(), sensor.getName(), sensor.getType());
    }
}
