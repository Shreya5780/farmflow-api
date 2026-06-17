package com.shreya.farmflow_api.dto;

import com.shreya.farmflow_api.model.ActivityStatus;
import com.shreya.farmflow_api.model.ActivityType;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityRequest {

    private ActivityType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private ActivityStatus status;
    private String description;
    private Integer riskScore;
    private String riskLevel;
    private String riskReason;
}
