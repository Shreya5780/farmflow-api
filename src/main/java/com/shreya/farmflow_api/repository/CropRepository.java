package com.shreya.farmflow_api.repository;

import com.shreya.farmflow_api.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, String> {
    List<Crop> findByFarmId(String farmId);
}
