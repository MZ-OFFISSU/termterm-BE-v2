package site.termterm.api.domain.comment.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.entity.ReportStatus;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r " +
            "JOIN FETCH r.comment ")
    List<Report> findAllWithComment();
}
