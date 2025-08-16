package com.carlosrios.surveys.infra.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TitleNotValidException extends RuntimeException {
    private final String message;
}
