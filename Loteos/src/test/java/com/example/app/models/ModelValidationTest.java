package com.example.app.models;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void requiresLoteoName() {
        Loteo loteo = new Loteo(" ");

        assertThat(validator.validate(loteo))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    void rejectsNegativeLoteSurface() {
        Lote lote = new Lote();
        lote.setNumeroCuenta("123");
        lote.setSuperficie(-1.0);

        assertThat(validator.validate(lote))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("superficie"));
    }
}
