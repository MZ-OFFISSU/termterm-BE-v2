package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.bookmark.entity.TermBookmark;

public interface TermBookmarkRepository extends JpaRepository<TermBookmark, Long>, TermBookmarkRepositoryCustom {
}
