package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;
import java.util.Optional;

public interface TermBookmarkRepository extends JpaRepository<TermBookmark, Long> {
    Optional<TermBookmark> findByTermAndMember(Term term, Member member);

    @Modifying
    @Query("DELETE FROM TermBookmark tb WHERE tb.id = :id")
    void deleteById(@Param("id") Long id);

    @Query(value = "SELECT t.term_id AS termId, t.name, t.description " +
            "FROM term_bookmark tb " +
            "INNER JOIN term t ON tb.term_id = t.term_id " +
            "WHERE tb.member_id = :memberId " +
            "ORDER BY RAND() LIMIT 10;", nativeQuery = true)
    List<FolderResponseDto.TermIdAndNameAndDescriptionDtoInterface> findTermIdAndNameAndDescriptionByMemberId(@Param("memberId") Long memberId);
}
