package com.shreya.farmflow_api.controller;

import com.shreya.farmflow_api.dto.CreateCropRequest;
import com.shreya.farmflow_api.dto.CropResponse;
import com.shreya.farmflow_api.dto.DtoMapper;
import com.shreya.farmflow_api.model.Crop;
import com.shreya.farmflow_api.model.Farm;
import com.shreya.farmflow_api.repository.CropRepository;
import com.shreya.farmflow_api.repository.FarmRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/farms/{farmId}/crops")
public class CropController {

    private final FarmRepository farmRepository;
    private final CropRepository cropRepository;

    public CropController(FarmRepository farmRepository, CropRepository cropRepository) {
        this.farmRepository = farmRepository;
        this.cropRepository = cropRepository;
    }

    @GetMapping
    public List<CropResponse> listCrops(@PathVariable String farmId) {
        return cropRepository.findByFarmId(farmId).stream()
                .map(DtoMapper::toCropResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CropResponse createCrop(@PathVariable String farmId, @Valid @RequestBody CreateCropRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        Crop crop = Crop.builder()
                .farm(farm)
                .name(request.getName())
                .sowDate(request.getSowDate())
                .expectedHarvestDate(request.getExpectedHarvestDate())
                .variety(request.getVariety())
                .estimatedYield(request.getEstimatedYield())
                .build();

        return DtoMapper.toCropResponse(cropRepository.save(crop));
    }
}
