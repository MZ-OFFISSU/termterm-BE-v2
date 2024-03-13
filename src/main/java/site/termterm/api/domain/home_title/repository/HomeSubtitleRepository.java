package site.termterm.api.domain.home_title.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.termterm.api.domain.home_title.entity.HomeSubtitle;

import java.util.List;
import java.util.Optional;

public interface HomeSubtitleRepository extends JpaRepository<HomeSubtitle, Long> {
    @Query("SELECT ht.subtitle FROM HomeSubtitle ht ORDER BY FUNCTION('RAND') ")
    List<String> getNRandom(Pageable pageable);

    Optional<HomeSubtitle> findBySubtitle(String subtitle);
}
