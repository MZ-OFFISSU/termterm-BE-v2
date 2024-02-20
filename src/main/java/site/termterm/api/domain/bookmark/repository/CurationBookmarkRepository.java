package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.member.entity.Member;

import java.util.Optional;

public interface CurationBookmarkRepository extends JpaRepository<CurationBookmark, Long> {
    Optional<CurationBookmark> findByCurationAndMember(Curation curation, Member member);
}
