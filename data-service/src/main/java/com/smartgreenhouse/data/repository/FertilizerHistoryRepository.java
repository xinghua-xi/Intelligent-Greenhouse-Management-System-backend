package com.smartgreenhouse.data.repository;

import com.smartgreenhouse.data.entity.FertilizerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FertilizerHistoryRepository extends JpaRepository<FertilizerHistory, Long> {
    
    /**
     * 获取最新一条记录
     */
    Optional<FertilizerHistory> findTopByOrderByCreateTimeDesc();

    /**
     * 获取指定时间之后的历史记录
     */
    List<FertilizerHistory> findByCreateTimeAfterOrderByCreateTimeAsc(LocalDateTime startTime);
}
