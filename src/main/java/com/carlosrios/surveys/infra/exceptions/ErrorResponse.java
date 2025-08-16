package com.carlosrios.surveys.infra.exceptions;

import java.util.Map;

public record ErrorResponse(Map<String, String> error) {
}
