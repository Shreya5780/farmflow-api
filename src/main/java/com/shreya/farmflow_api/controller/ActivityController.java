package com.shreya.farmflow_api.controller;

import com.shreya.farmflow_api.dto.ActivityResponse;
import com.shreya.farmflow_api.dto.CreateActivityRequest;
import com.shreya.farmflow_api.dto.DtoMapper;
import com.shreya.farmflow_api.dto.ReorderActivitiesRequest;
import com.shreya.farmflow_api.model.Activity;
import com.shreya.farmflow_api.model.ActivityStatus;
import com.shreya.farmflow_api.model.Crop;
import com.shreya.farmflow_api.repository.ActivityRepository;
import com.shreya.farmflow_api.repository.CropRepository;
import com.shreya.farmflow_api.service.RiskService;
import com.shreya.farmflow_api.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final CropRepository cropRepository;
    private final ActivityRepository activityRepository;
    private final WeatherService weatherService;
    private final RiskService riskService;

    public ActivityController(CropRepository cropRepository,
                              ActivityRepository activityRepository,
                              WeatherService weatherService,
                              RiskService riskService) {
        this.cropRepository = cropRepository;
        this.activityRepository = activityRepository;
        this.weatherService = weatherService;
        this.riskService = riskService;
    }

    @GetMapping("/crops/{cropId}/activities")
    public List<ActivityResponse> listActivities(@PathVariable String cropId) {
        verifyCropExists(cropId);
        return activityRepository.findByCropIdOrderBySortOrderAsc(cropId).stream()
                .map(DtoMapper::toActivityResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/crops/{cropId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse createActivity(@PathVariable String cropId, @Valid @RequestBody CreateActivityRequest request) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found"));

        int nextOrder = activityRepository.findByCropId(cropId).stream()
                .map(Activity::getSortOrder)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;

        Activity activity = Activity.builder()
                .crop(crop)
                .type(request.getType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ActivityStatus.PLANNED)
                .description(request.getDescription())
                .riskScore(0)
                .riskLevel("LOW")
                .riskReason(null)
                .sortOrder(nextOrder)
                .build();

        return DtoMapper.toActivityResponse(activityRepository.save(activity));
    }

    @PutMapping("/activities/{activityId}")
    public ActivityResponse updateActivity(@PathVariable String activityId, @RequestBody com.shreya.farmflow_api.dto.UpdateActivityRequest request) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        if (request.getType() != null) {
            activity.setType(request.getType());
        }
        if (request.getStartDate() != null) {
            activity.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            activity.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            activity.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getRiskScore() != null) {
            activity.setRiskScore(request.getRiskScore());
        }
        if (request.getRiskLevel() != null) {
            activity.setRiskLevel(request.getRiskLevel());
        }
        if (request.getRiskReason() != null) {
            activity.setRiskReason(request.getRiskReason());
        }

        return DtoMapper.toActivityResponse(activityRepository.save(activity));
    }

    @DeleteMapping("/activities/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable String activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        activityRepository.deleteById(activityId);
    }

    @PutMapping("/crops/{cropId}/activities/reorder")
    public void reorderActivities(@PathVariable String cropId, @Valid @RequestBody ReorderActivitiesRequest request) {
        verifyCropExists(cropId);
        List<Activity> activities = activityRepository.findByCropId(cropId);
        var activityById = activities.stream().collect(Collectors.toMap(Activity::getId, a -> a));

        int order = 1;
        for (String id : request.getOrderedIds()) {
            Activity activity = activityById.get(id);
            if (activity != null) {
                activity.setSortOrder(order++);
            }
        }

        activityRepository.saveAll(activities);
    }

    @GetMapping("/crops/{cropId}/risk")
    public List<ActivityResponse> assessRisk(@PathVariable String cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found"));

        var weatherForecast = weatherService.getWeather(crop.getFarm().getLatitude(), crop.getFarm().getLongitude());
        if (weatherForecast.isEmpty()) {
            return activityRepository.findByCropIdOrderBySortOrderAsc(cropId).stream()
                    .map(DtoMapper::toActivityResponse)
                    .collect(Collectors.toList());
        }

        return activityRepository.findByCropIdOrderBySortOrderAsc(cropId).stream()
                .map(activity -> riskService.assessRisk(activity, weatherForecast))
                .collect(Collectors.toList());
    }

    private void verifyCropExists(String cropId) {
        if (!cropRepository.existsById(cropId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found");
        }
    }
}
