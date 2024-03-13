package site.termterm.api.domain.term.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

import site.termterm.api.db_migration.MigrationRequestDto;
import site.termterm.api.domain.curation.dto.CurationResponseDto;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long>, Dao {
    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermIdAndNameAndBookmarkStatusResponseDto(t.id, t.name, tb) " +
            "FROM Term t " +
            "LEFT JOIN TermBookmark tb " +
            "ON tb.termId = t.id AND tb.member.id = :memberId " +
            "WHERE t.name LIKE CONCAT('%', :name, '%') ")
    List<TermIdAndNameAndBookmarkStatusResponseDto> getSearchResults(@Param("name") String name, @Param("memberId") Long memberId);

    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermIdAndNameResponseDto(t.id, t.name) " +
            "FROM Term t " +
            "WHERE t.id = :termId")
    Optional<TermIdAndNameResponseDto> findIdAndNameById(@Param("termId") Long termId);

    @Query("SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$TermDetailInfoDto(t) " +
            "FROM Term t " +
            "WHERE t.id IN :termIdList")
    List<FolderResponseDto.TermDetailInfoDto> findTermsByIdListAlwaysBookmarked(@Param("termIdList") List<Long> termIdList);

    @Query("SELECT new site.termterm.api.domain.curation.dto.CurationResponseDto$CurationDetailResponseDto$TermSimpleDto(t.id, t.name, t.description, tb) " +
            "FROM Term t " +
            "LEFT JOIN TermBookmark tb " +
            "ON tb.termId = t.id AND tb.member.id = :memberId " +
            "WHERE t.id IN :termIdList")
    List<CurationResponseDto.CurationDetailResponseDto.TermSimpleDto> getTermsSimpleDtoListByIdList(@Param("termIdList") List<Long> termIdList, @Param("memberId") Long memberId);

    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermDetailDto(t.id, t.name, t.description, t.categories, tb) " +
        "FROM Term t " +
        "LEFT JOIN TermBookmark tb " +
        "ON tb.termId = t.id AND tb.member.id = :memberId " +
        "WHERE t.id = :termId ")
    TermDetailDto getTermDetailDto(@Param("termId") Long termId, @Param("memberId") Long memberId);

    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermSimpleDto(t.id, t.name, t.description, tb) " +
            "FROM Term t " +
            "LEFT JOIN TermBookmark tb " +
            "ON tb.termId = t.id AND tb.member.id = :memberId " +
            "WHERE t.id IN :termIdList")
    List<TermSimpleDto> getTermsByIdList(@Param("termIdList") List<Long> termIdList, @Param("memberId") Long memberId);

    @Query("SELECT t FROM Term t WHERE t.id IN :termIdList")
    List<Term> getTermsByIdListExceptBookmarkStatus(@Param("termIdList") List<Long> termIdList);

    @Query("SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$FolderDetailResponseDto$TermIdAndNameDto(t.id, t.name) " +
            "FROM Term t WHERE t.id IN :termIdList ORDER BY FUNCTION('FIND_IN_SET', t.id, :termIdListString)")
    List<FolderResponseDto.FolderDetailResponseDto.TermIdAndNameDto> getTermsByIdListOrderByFindInSet(@Param("termIdList") List<Long> termIdList, @Param("termIdListString") String termIdListString);

    @Query("SELECT t FROM Term t ORDER BY FUNCTION('RAND') ")
    List<Term> getNRandomTerms(Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO term(term_id, name, description, categories) " +
            "VALUES (:#{#termDto.id}, :#{#termDto.name}, :#{#termDto.description}, '[]')", nativeQuery = true)
    void saveWithId(@Param("termDto") MigrationRequestDto.TermDto termDto);

}
