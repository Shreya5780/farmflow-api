package com.shreya.farmflow_api.repository;

import com.shreya.farmflow_api.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, String> {
}
