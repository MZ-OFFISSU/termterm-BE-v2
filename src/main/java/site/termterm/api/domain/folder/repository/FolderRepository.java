package site.termterm.api.domain.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.folder.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT COUNT(f) FROM Folder f WHERE f.member.id = :memberId")
    Integer countByMemberId(@Param("memberId") Long memberId);

}
