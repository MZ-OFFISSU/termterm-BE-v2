package site.termterm.api.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.term.dto.TermResponseDto;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$TermDetailInfoDto$CommentDetailInfoDto(c, c.member.nickname, c.member.job, c.member.profileImg, cl.status, c.termId) " +
            "FROM Comment c " +
            "INNER JOIN Member m ON m.id = c.member.id " +
            "LEFT JOIN CommentLike cl " +
            "ON c.id = cl.comment.id AND cl.member.id = :loginMemberId " +
            "WHERE c.termId IN :termIdList AND (c.status = :status1 OR c.status = :status2) ")
    List<FolderResponseDto.TermDetailInfoDto.CommentDetailInfoDto> getCommentDetailByTermIdList(@Param("termIdList") List<Long> termIdList, @Param("loginMemberId") Long memberId, @Param("status1") CommentStatus status1, @Param("status2") CommentStatus status2);

    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermDetailDto$CommentDto(c.id, c.content, c.likeCnt, m.nickname, m.job, m.profileImg, c.createdDate, c.source, cl.status) " +
            "FROM Comment c " +
            "INNER JOIN Member m ON m.id = c.member.id " +
            "LEFT JOIN CommentLike cl " +
            "ON c.id = cl.comment.id AND cl.member.id = :loginMemberId " +
            "WHERE c.termId = :termId AND (c.status = :status1 OR c.status = :status2) ")
    List<TermResponseDto.TermDetailDto.CommentDto> getCommentDtoByTermIdAndMemberId(
            @Param("termId") Long termId, @Param("loginMemberId") Long loginMemberId,
            @Param("status1") CommentStatus status1, @Param("status2") CommentStatus status2);

    Long countByTermId(Long id);

}
