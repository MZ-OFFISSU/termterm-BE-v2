package site.termterm.api.domain.curation.repository;

import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.category.CategoryEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static site.termterm.api.domain.curation.dto.CurationDatabaseDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

public interface CurationRepositoryCustom {
    Optional<CurationInfoWithBookmarkDto> findByIdWithBookmarked(Long curationId, Long memberId);
    Set<CurationSimpleResponseDtoNamedStatus> getArchivedCurationsWithBookmarked(Long memberId, BookmarkStatus status);
    String getTitleById(Long curationId);
    List<Object[]> getCurationsByCategory(CategoryEnum category, Long memberId);
    List<Object[]> getCurationDtoListByCategoriesExceptMainCuration(Long mainCurationId, List<CategoryEnum> categories, Long memberId);
    List<Object[]> getCurationDtoListByCategoriesLimit6(List<CategoryEnum> categories, Long memberId);
}
