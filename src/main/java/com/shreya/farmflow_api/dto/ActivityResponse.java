package com.shreya.farmflow_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ActivityResponse(
        String id,
        String cropId,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String description,
        Integer riskScore,
        String riskLevel,
        String riskReason,
        LocalDateTime createdAt,
        Integer sortOrder
) {
}
