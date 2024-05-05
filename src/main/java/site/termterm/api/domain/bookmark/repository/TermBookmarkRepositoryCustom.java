package site.termterm.api.domain.bookmark.repository;

import org.springframework.data.domain.Pageable;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;

public interface TermBookmarkRepositoryCustom {
    Optional<TermBookmark> findByTermIdAndMember(Long termId, Member member);
    List<TermIdAndNameAndDescriptionDto> findTermIdAndNameAndDescriptionByMemberId(Long memberId, Pageable pageable);
}
