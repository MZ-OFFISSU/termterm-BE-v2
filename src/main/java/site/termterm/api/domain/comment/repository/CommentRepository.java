package site.termterm.api.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.folder.dto.FolderResponseDto;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$TermDetailInfoDto$CommentDetailInfoDto(c, c.member.nickname, c.member.job, c.member.profileImg, cl.status, c.termId) " +
            "FROM Comment c " +
            "INNER JOIN Member m ON m.id = c.member.id " +
            "LEFT JOIN CommentLike cl " +
            "ON c.id = cl.comment.id AND cl.member.id = :loginMemberId " +
            "WHERE c.termId IN :termIdList")
    List<FolderResponseDto.TermDetailInfoDto.CommentDetailInfoDto> getCommentDetailByTermIdList(@Param("termIdList") List<Long> termIdList, @Param("loginMemberId") Long memberId);
}
