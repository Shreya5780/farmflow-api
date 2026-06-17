package com.shreya.farmflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCropRequest {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate sowDate;

    @NotNull
    private LocalDate expectedHarvestDate;

    @NotBlank
    private String variety;

    @NotNull
    private Integer estimatedYield;
}
