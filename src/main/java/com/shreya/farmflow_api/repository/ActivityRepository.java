package com.shreya.farmflow_api.repository;

import com.shreya.farmflow_api.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, String> {
    List<Activity> findByCropIdOrderBySortOrderAsc(String cropId);
    List<Activity> findByCropId(String cropId);
}
