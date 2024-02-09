package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;

import java.util.Optional;

public interface TermBookmarkRepository extends JpaRepository<TermBookmark, Long> {
    Optional<TermBookmark> findByTermAndMember(Term term, Member member);

//    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Modifying
    @Query("DELETE FROM TermBookmark tb WHERE tb.id = :id")
    void deleteById(@Param("id") Long id);
}
