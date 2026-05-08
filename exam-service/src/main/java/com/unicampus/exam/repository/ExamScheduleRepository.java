package com.unicampus.exam.repository;

import com.unicampus.exam.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, UUID> {
    List<ExamSchedule> findByCourseId(UUID courseId);

    @Query("SELECT e FROM ExamSchedule e WHERE e.examDate >= :startDate AND e.examDate <= :endDate")
    List<ExamSchedule> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
