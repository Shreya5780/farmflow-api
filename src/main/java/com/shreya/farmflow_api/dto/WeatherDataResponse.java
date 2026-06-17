package com.shreya.farmflow_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WeatherDataResponse(
        LocalDate date,
        double tempC,
        int tempRounded,
        int humidity,
        double windKph,
        double precipitationProbability,
        String condition,
        String conditionDescription,
        LocalDateTime timestamp
) {
}
