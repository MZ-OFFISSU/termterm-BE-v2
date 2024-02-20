package site.termterm.api.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

import site.termterm.api.domain.curation.dto.CurationResponseDto;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermIdAndNameAndBookmarkStatusResponseDto(t.id, t.name) " +
            "FROM Term t " +
            "WHERE t.name LIKE CONCAT('%', :name, '%') ")
    List<TermIdAndNameAndBookmarkStatusResponseDto> getSearchResults(@Param("name") String name);

    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermIdAndNameResponseDto(t.id, t.name) " +
            "FROM Term t " +
            "WHERE t.id = :termId")
    Optional<TermIdAndNameResponseDto> findIdAndNameById(@Param("termId") Long termId);

    @Query("SELECT new site.termterm.api.domain.folder.dto.FolderResponseDto$TermDetailInfoDto(t) " +
            "FROM Term t " +
            "WHERE t.id IN :termIdList")
    List<FolderResponseDto.TermDetailInfoDto> findTermsByIdList(@Param("termIdList") List<Long> termIdList);

    @Query("SELECT new site.termterm.api.domain.curation.dto.CurationResponseDto$CurationDetailResponseDto$TermSimpleDto(t.id, t.name, t.description, tb) " +
            "FROM Term t " +
            "LEFT JOIN TermBookmark tb " +
            "ON tb.termId = t.id " +
            "WHERE t.id IN :termIdList")
    List<CurationResponseDto.CurationDetailResponseDto.TermSimpleDto> getTermsSimpleDtoListByIdList(@Param("termIdList") List<Long> termIdList);
}
