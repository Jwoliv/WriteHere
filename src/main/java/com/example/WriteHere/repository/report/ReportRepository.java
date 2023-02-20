package com.example.WriteHere.repository.report;

import com.example.WriteHere.model.report.AbstractReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository<T extends AbstractReport> extends JpaRepository<T, Long> {
}
