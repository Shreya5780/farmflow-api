package com.shreya.farmflow_api.service;

import com.shreya.farmflow_api.dto.ActivityResponse;
import com.shreya.farmflow_api.dto.WeatherDataResponse;
import com.shreya.farmflow_api.model.Activity;
import org.springframework.stereotype.Service;

@Service
public class RiskService {

    public ActivityResponse assessRisk(Activity activity, WeatherDataResponse weather) {
        int riskScore = 20;
        String riskLevel = "LOW";
        String riskReason = "Normal weather conditions.";

        if (weather.precipitationProbability() >= 0.7 || weather.windKph() >= 20 || weather.tempC() <= 2 || weather.tempC() >= 35) {
            riskLevel = "HIGH";
            riskScore = 80;
            riskReason = "High weather risk from precipitation, wind, or temperature extremes.";
        } else if (weather.precipitationProbability() >= 0.4 || weather.windKph() >= 12 || weather.tempC() <= 5 || weather.tempC() >= 30) {
            riskLevel = "MEDIUM";
            riskScore = 50;
            riskReason = "Moderate weather risk for planned field activities.";
        }

        return new ActivityResponse(
                activity.getId(),
                activity.getCrop().getId(),
                activity.getType().name(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getStatus().name(),
                activity.getDescription(),
                riskScore,
                riskLevel,
                riskReason,
                activity.getCreatedAt(),
                activity.getSortOrder()
        );
    }
}
