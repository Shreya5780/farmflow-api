package com.shreya.farmflow_api.controller;

import com.shreya.farmflow_api.dto.CreateFarmRequest;
import com.shreya.farmflow_api.dto.DtoMapper;
import com.shreya.farmflow_api.dto.FarmResponse;
import com.shreya.farmflow_api.model.Farm;
import com.shreya.farmflow_api.repository.FarmRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmRepository farmRepository;

    public FarmController(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @GetMapping
    public List<FarmResponse> listFarms() {
        return farmRepository.findAll().stream()
                .map(DtoMapper::toFarmResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FarmResponse createFarm(@Valid @RequestBody CreateFarmRequest request) {
        var farm = Farm.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .region(request.getRegion())
                .build();

        return DtoMapper.toFarmResponse(farmRepository.save(farm));
    }
}
