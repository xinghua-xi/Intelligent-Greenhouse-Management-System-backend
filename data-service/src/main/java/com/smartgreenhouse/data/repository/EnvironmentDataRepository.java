package com.smartgreenhouse.data.repository;

import com.smartgreenhouse.data.entity.EnvironmentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface EnvironmentDataRepository extends JpaRepository<EnvironmentData, Long> {
    
    List<EnvironmentData> findByGreenhouseIdAndRecordedAtAfterOrderByRecordedAtAsc(
            String greenhouseId, LocalDateTime after);
    
    List<EnvironmentData> findByRecordedAtAfterOrderByRecordedAtAsc(LocalDateTime after);
    
    @Query("SELECT e FROM EnvironmentData e WHERE e.greenhouseId = ?1 ORDER BY e.recordedAt DESC LIMIT 20")
    List<EnvironmentData> findLatestByGreenhouseId(String greenhouseId);
    
    @Query("SELECT e FROM EnvironmentData e ORDER BY e.recordedAt DESC LIMIT 20")
    List<EnvironmentData> findLatest();
}
