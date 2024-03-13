package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface TermBookmarkRepository extends JpaRepository<TermBookmark, Long> {
    @Query("SELECT tb FROM TermBookmark tb WHERE tb.termId = :termId AND tb.member = :member")
    Optional<TermBookmark> findByTermIdAndMember(@Param("termId") Long termId, @Param("member") Member member);

    @Query("SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$TermIdAndNameAndDescriptionDto(t.id, t.name, t.description) " +
            "FROM TermBookmark tb " +
            "INNER JOIN Term t ON t.id = tb.termId " +
            "WHERE tb.member.id = :memberId " +
            "ORDER BY FUNCTION('RAND') ")
    List<FolderResponseDto.TermIdAndNameAndDescriptionDto> findTermIdAndNameAndDescriptionByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
