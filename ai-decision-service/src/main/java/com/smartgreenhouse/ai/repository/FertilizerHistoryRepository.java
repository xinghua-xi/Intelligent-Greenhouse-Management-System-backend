package com.smartgreenhouse.ai.repository;

import com.smartgreenhouse.ai.entity.FertilizerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 施肥历史记录 Repository
 */
@Repository
public interface FertilizerHistoryRepository extends JpaRepository<FertilizerHistory, Long> {

    /**
     * 按周数查询历史记录
     */
    List<FertilizerHistory> findByWeek(Integer week);

    /**
     * 按时间倒序获取最近的记录
     */
    List<FertilizerHistory> findTop10ByOrderByCreateTimeDesc();
}
