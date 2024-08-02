package com.example.petstable.domain.report.repository;

import com.example.petstable.domain.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
}
