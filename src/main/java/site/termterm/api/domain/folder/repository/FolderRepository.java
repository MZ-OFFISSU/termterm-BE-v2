package site.termterm.api.domain.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.db_migration.MigrationRequestDto;
import site.termterm.api.domain.folder.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT COUNT(f) FROM Folder f WHERE f.member.id = :memberId")
    Integer countByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query(value = "INSERT INTO folder(save_limit, folder_id, member_id, description, title, term_ids) " +
            "VALUES (:#{#folderDto.saveLimit}, :#{#folderDto.id}, :#{#folderDto.memberId}, :#{#folderDto.description}, :#{#folderDto.title}, '[]')", nativeQuery = true)
    void saveWithId(@Param("folderDto") MigrationRequestDto.FolderDto folderDto);

}
