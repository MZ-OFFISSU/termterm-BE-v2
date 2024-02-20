package site.termterm.api.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndEmail(@Param("socialId") String socialId, @Param("email") String email);
    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);
    Boolean existsByNicknameIgnoreCase(@Param("nickname") String nickname);

    @Query("SELECT m.folders FROM Member m WHERE m.id = :memberId")
    List<Folder> findFoldersById(@Param("memberId") Long memberId);

    @Query("SELECT m.folderLimit FROM Member m WHERE m.id = :memberId")
    Integer findFolderLimitById(@Param("memberId") Long memberId);

    @Query("SELECT m.categories FROM Member m WHERE m.id = :memberId")
    List<ArrayList<CategoryEnum>> getCategoriesById(@Param("memberId") Long memberId);
}
