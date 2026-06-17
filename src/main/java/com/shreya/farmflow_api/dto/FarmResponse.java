package com.shreya.farmflow_api.dto;

import java.time.LocalDateTime;

public record FarmResponse(
        String id,
        String userId,
        String name,
        double latitude,
        double longitude,
        String region,
        LocalDateTime createdAt
) {
}
