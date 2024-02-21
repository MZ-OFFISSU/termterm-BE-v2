package site.termterm.api.domain.daily_term.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.daily_term.entity.DailyTerm;

public interface DailyTermRepository extends JpaRepository<DailyTerm, Long> {
}
