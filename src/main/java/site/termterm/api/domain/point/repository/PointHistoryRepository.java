package site.termterm.api.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.point.entity.PointHistory;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByMemberOrderByDate(Member member);

}
