package com.shreya.farmflow_api.dto;

import com.shreya.farmflow_api.model.Activity;
import com.shreya.farmflow_api.model.Crop;
import com.shreya.farmflow_api.model.Farm;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static FarmResponse toFarmResponse(Farm farm) {
        return new FarmResponse(
                farm.getId(),
                farm.getUserId(),
                farm.getName(),
                farm.getLatitude(),
                farm.getLongitude(),
                farm.getRegion(),
                farm.getCreatedAt()
        );
    }

    public static CropResponse toCropResponse(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getFarm().getId(),
                crop.getName(),
                crop.getSowDate(),
                crop.getExpectedHarvestDate(),
                crop.getVariety(),
                crop.getEstimatedYield(),
                crop.getCreatedAt()
        );
    }

    public static ActivityResponse toActivityResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getCrop().getId(),
                activity.getType().name(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getStatus().name(),
                activity.getDescription(),
                activity.getRiskScore(),
                activity.getRiskLevel(),
                activity.getRiskReason(),
                activity.getCreatedAt(),
                activity.getSortOrder()
        );
    }
}
