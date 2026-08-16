package com.aldisued.iot.monitoring.messaging;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.dto.validator.DtoValidator;
import com.aldisued.iot.monitoring.service.AlertService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AlertKafkaListenerTest {

    private static final AlertDto ALERT_DTO = new AlertDto(UUID.randomUUID(), "", LocalDateTime.now());

    @Mock
    private AlertService alertService;
    @Mock
    private DtoValidator dtoValidator;

    @InjectMocks
    private AlertKafkaListener underTest;

    @Test
    void testListenShouldNotCallAlertServiceWhenValidationFails() {
        // Given
        doThrow(ConstraintViolationException.class).when(dtoValidator).validateDto(ALERT_DTO);

        // When
        underTest.listen(ALERT_DTO);

        // Then
        verify(dtoValidator).validateDto(ALERT_DTO);
        verifyNoMoreInteractions(dtoValidator);
        verifyNoInteractions(alertService);
    }

    @Test
    void testListenShouldCallAlertService() {
        // Given

        // When
        underTest.listen(ALERT_DTO);

        // Then
        verify(dtoValidator).validateDto(ALERT_DTO);
        verify(alertService).saveAlert(ALERT_DTO);
        verifyNoMoreInteractions(dtoValidator, alertService);
    }
}