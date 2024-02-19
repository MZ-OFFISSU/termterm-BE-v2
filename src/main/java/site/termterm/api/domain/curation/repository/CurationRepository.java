package site.termterm.api.domain.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.curation.dto.CurationDatabaseDto;
import site.termterm.api.domain.curation.entity.Curation;

import java.util.Optional;

public interface CurationRepository extends JpaRepository<Curation, Long>, Dao {

    @Query("SELECT new site.termterm.api.domain.curation.dto.CurationDatabaseDto$CurationInfoWithBookmarkDto(c.title, c.cnt, c.description, c.thumbnail, c.tags, c.termIds, c.categories, cb.status) " +
            "FROM Curation c " +
            "LEFT JOIN CurationBookmark cb " +
            "ON cb.curation.id = c.id AND cb.member.id = :memberId " +
            "WHERE c.id = :curationId ")
    Optional<CurationDatabaseDto.CurationInfoWithBookmarkDto> findByIdWithBookmarked(@Param("curationId") Long curationId, @Param("memberId") Long memberId);
}
