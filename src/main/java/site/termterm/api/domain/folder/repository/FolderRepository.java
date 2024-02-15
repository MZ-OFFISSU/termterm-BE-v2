package site.termterm.api.domain.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.term.entity.Term;

import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;
import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT COUNT(f) FROM Folder f WHERE f.member.id = :memberId")
    Integer countByMemberId(@Param("memberId") Long memberId);

    @Query("""
        SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$TermDetailInfoDto$CommentDetailInfoDto(c.id, c.content, c.likeCnt, c.member.name, c.member.job, c.member.profileImg, c.createdDate, c.source, cl.status)
        FROM Comment c
        LEFT JOIN CommentLike cl
        ON c.id = cl.comment.id AND cl.member.id = :memberId
      
    """)
    List<Object> fetchTest1(@Param("memberId") Long memberId);


}
