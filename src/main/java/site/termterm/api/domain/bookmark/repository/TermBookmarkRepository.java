package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;

import java.util.Optional;

public interface TermBookmarkRepository extends JpaRepository<TermBookmark, Long> {
    Optional<TermBookmark> findByTermAndMember(Term term, Member member);
}
