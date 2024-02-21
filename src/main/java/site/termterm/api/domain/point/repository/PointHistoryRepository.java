package site.termterm.api.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.point.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
