package site.termterm.api.domain.comment.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.comment.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
