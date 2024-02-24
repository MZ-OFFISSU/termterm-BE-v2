package site.termterm.api.domain.home_title.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.termterm.api.domain.home_title.entity.HomeSubtitle;

import java.util.Optional;

public interface HomeSubtitleRepository extends JpaRepository<HomeSubtitle, Long> {
    @Query(nativeQuery = true,
            value = "select t.subtitle from home_subtitle t ORDER BY RAND() LIMIT 1")
    Optional<String> getRandomOne();

    Optional<HomeSubtitle> findBySubtitle(String subtitle);
}
