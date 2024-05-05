package site.termterm.api.domain.term.repository;

import org.springframework.data.domain.Pageable;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;

import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.FolderDetailResponseDto.*;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

public interface TermRepositoryCustom {
    List<Object[]> getTermsByCategories(List<CategoryEnum> categories, Long memberId);
    List<Object[]> getTermsByCategoriesRandom4(List<CategoryEnum> categories, Long memberId);
    List<TermIdAndNameAndBookmarkStatusResponseDto> getSearchResults(String name, Long memberId);
    List<TermDetailInfoDto> findTermsByIdListAlwaysBookmarked(List<Long> termIdList);
    List<CurationDetailResponseDto.TermSimpleDto> getTermsSimpleDtoListByIdList(List<Long> termIdList, Long memberId);
    TermDetailDto getTermDetailDto(Long termId, Long memberId);
    List<TermSimpleDto> getTermsByIdList(List<Long> termIdList, Long memberId);
    List<Term> getTermsByIdListExceptBookmarkStatus(List<Long> termIdList);
    List<TermIdAndNameDto> getTermsByIdListOrderByFindInSet(List<Long> termIdList, String termIdListString);
    List<Term> getNRandomTerms(Pageable pageable);
}
