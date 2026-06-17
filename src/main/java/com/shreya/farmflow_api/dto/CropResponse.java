package com.shreya.farmflow_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CropResponse(
        String id,
        String farmId,
        String name,
        LocalDate sowDate,
        LocalDate expectedHarvestDate,
        String variety,
        Integer estimatedYield,
        LocalDateTime createdAt
) {
}
