package site.termterm.api.domain.comment.repository;

import site.termterm.api.domain.comment.entity.CommentStatus;

import java.util.List;

import static site.termterm.api.domain.comment.dto.CommentResponseDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.TermDetailInfoDto.*;
import static site.termterm.api.domain.term.dto.TermResponseDto.TermDetailDto.*;

public interface CommentRepositoryCustom {
    List<CommentDetailInfoDto> getCommentDetailByTermIdList(
            List<Long> termIdList, Long memberId, CommentStatus status1, CommentStatus status2);

    List<CommentDto> getCommentDtoByTermIdAndMemberId(
            Long termId, Long loginMemberId, CommentStatus status1, CommentStatus status2);

    List<CommentInfoForAdminDto> getCommentListForAdmin();
}
