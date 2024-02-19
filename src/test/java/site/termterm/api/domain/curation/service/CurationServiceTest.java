package site.termterm.api.domain.curation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CurationServiceTest extends DummyObject {

    @InjectMocks
    private CurationService curationService;

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CurationBookmarkRepository curationBookmarkRepository;

    @DisplayName("큐레이션 북마크 성공 - NO -> YES")
    @Test
    public void curation_bookmark_success_test() throws Exception{
        //given
        Curation curation = newMockCuration(1L, "큐레이션1", List.of(1L, 3L, 5L), List.of("태그1"), List.of(CategoryEnum.IT));
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        CurationBookmark curationBookmark = CurationBookmark.of(curation, sinner);
        curationBookmark.setStatus(BookmarkStatus.NO);

        //stub
        when(curationRepository.getReferenceById(any())).thenReturn(curation);
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(curationBookmarkRepository.findByCurationAndMember(any(), any())).thenReturn(Optional.of(curationBookmark));

        //when
        CurationBookmark returnedCurationBookmark = curationService.bookmark(1L, 1L);

        //then
        assertThat(returnedCurationBookmark.getStatus()).isEqualTo(BookmarkStatus.YES);

    }

    @DisplayName("큐레이션 북마크 실패 - 이미 YES")
    @Test
    public void curation_bookmark_fail_test() throws Exception{
        //given
        Curation curation = newMockCuration(1L, "큐레이션1", List.of(1L, 3L, 5L), List.of("태그1"), List.of(CategoryEnum.IT));
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        CurationBookmark curationBookmark = CurationBookmark.of(curation, sinner);

        //stub
        when(curationRepository.getReferenceById(any())).thenReturn(curation);
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(curationBookmarkRepository.findByCurationAndMember(any(), any())).thenReturn(Optional.of(curationBookmark));

        //when

        //then
        assertThrows(CustomApiException.class, () -> curationService.bookmark(1L, 1L));

    }

    @DisplayName("큐레이션 북마크 취소 성공")
    @Test
    public void curation_unBookmark_success_test() throws Exception{
        //given
        Curation curation = newMockCuration(1L, "큐레이션1", List.of(1L, 3L, 5L), List.of("태그1"), List.of(CategoryEnum.IT));
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        CurationBookmark curationBookmark = CurationBookmark.of(curation, sinner);
        curationBookmark.setStatus(BookmarkStatus.YES);

        //stub
        when(curationRepository.getReferenceById(any())).thenReturn(curation);
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(curationBookmarkRepository.findByCurationAndMember(any(), any())).thenReturn(Optional.of(curationBookmark));

        //when
        CurationBookmark returnedCurationBookmark = curationService.unBookmark(1L, 1L);

        //then
        assertThat(returnedCurationBookmark.getStatus()).isEqualTo(BookmarkStatus.NO);

    }

    @DisplayName("큐레이션 북마크 취소 실패 - 이미 NO")
    @Test
    public void curation_unBookmark_fail_test() throws Exception{
        //given
        Curation curation = newMockCuration(1L, "큐레이션1", List.of(1L, 3L, 5L), List.of("태그1"), List.of(CategoryEnum.IT));
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        CurationBookmark curationBookmark = CurationBookmark.of(curation, sinner);
        curationBookmark.setStatus(BookmarkStatus.NO);

        //stub
        when(curationRepository.getReferenceById(any())).thenReturn(curation);
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(curationBookmarkRepository.findByCurationAndMember(any(), any())).thenReturn(Optional.of(curationBookmark));

        //when

        //then
        assertThrows(CustomApiException.class, () -> curationService.unBookmark(1L, 1L));

    }

    @DisplayName("Category 문자열을 CategoryEnum 변환에 성공한다 - 카테고리별 큐레이션 목록 조회")
    @Test
    public void convert_category_enum_success_test() throws Exception{
        //given

        //stub
        when(curationRepository.getCurationsByCategory(any(), any())).thenReturn(Collections.emptyList());

        //when

        //then
        assertDoesNotThrow(() -> curationService.getCurationByCategory("IT", 1L));

    }

    @DisplayName("Category 문자열을 CategoryEnum 변환에 실패한다 - 카테고리별 큐레이션 목록 조회")
    @Test
    public void convert_category_enum_fail_test() throws Exception{
        //then
        assertThrows(CustomApiException.class, () -> curationService.getCurationByCategory("Nothing", 1L));

    }

}