package com.aldisued.iot.monitoring.dto.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DtoValidator {

    private final Validator validator;

    public DtoValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateDto(Object dto) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(dto);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }
    }
}
