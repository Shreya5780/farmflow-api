package com.shreya.farmflow_api.service;

import com.shreya.farmflow_api.dto.ActivityResponse;
import com.shreya.farmflow_api.dto.WeatherDataResponse;
import com.shreya.farmflow_api.model.Activity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskService {

    private record Score(int riskScore, String riskLevel, String riskReason) {}

    /**
     * Assess risk against the forecast days overlapping the activity's [startDate, endDate]
     * window, taking the worst matching day. When no forecast day overlaps (the activity is
     * beyond the ~5-day forecast horizon), returns a neutral "no forecast" result.
     */
    public ActivityResponse assessRisk(Activity activity, List<WeatherDataResponse> forecast) {
        List<WeatherDataResponse> overlapping = forecast.stream()
                .filter(w -> !w.date().isBefore(activity.getStartDate()) && !w.date().isAfter(activity.getEndDate()))
                .toList();

        if (overlapping.isEmpty()) {
            return toResponse(activity, new Score(0, "LOW",
                    "No weather forecast available for this period (beyond the forecast horizon)."));
        }

        Score worst = overlapping.stream()
                .map(this::score)
                .reduce((a, b) -> b.riskScore() > a.riskScore() ? b : a)
                .orElseThrow();

        return toResponse(activity, worst);
    }

    /** Backward-compatible single-day assessment. */
    public ActivityResponse assessRisk(Activity activity, WeatherDataResponse weather) {
        return toResponse(activity, score(weather));
    }

    private Score score(WeatherDataResponse weather) {
        if (weather.precipitationProbability() >= 0.7 || weather.windKph() >= 20 || weather.tempC() <= 2 || weather.tempC() >= 35) {
            return new Score(80, "HIGH", "High weather risk from precipitation, wind, or temperature extremes.");
        }
        if (weather.precipitationProbability() >= 0.4 || weather.windKph() >= 12 || weather.tempC() <= 5 || weather.tempC() >= 30) {
            return new Score(50, "MEDIUM", "Moderate weather risk for planned field activities.");
        }
        return new Score(20, "LOW", "Normal weather conditions.");
    }

    private ActivityResponse toResponse(Activity activity, Score score) {
        return new ActivityResponse(
                activity.getId(),
                activity.getCrop().getId(),
                activity.getType().name(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getStatus().name(),
                activity.getDescription(),
                score.riskScore(),
                score.riskLevel(),
                score.riskReason(),
                activity.getCreatedAt(),
                activity.getSortOrder()
        );
    }
}
