package site.termterm.api.domain.term.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.daily_term.entity.DailyTerm;
import site.termterm.api.domain.daily_term.repository.DailyTermRepository;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TermServiceTest extends DummyObject {
    @InjectMocks
    TermService termService;

    @Mock
    private TermRepository termRepository;
    @Mock
    private DailyTermRepository dailyTermRepository;
    @Mock
    private MemberRepository memberRepository;

    @DisplayName("오늘의 용어를 생성하여 응답한다 - 1.존재하고 날짜도 오늘이다.")
    @Test
    public void create_daily_term_success1_test() throws Exception{
        //given
        DailyTerm dailyTerm = newMockDailyTerm(1L, List.of(3L, 5L, 7L, 9L));
        List<TermSimpleDto> list = new ArrayList<>();
        for(Long id: dailyTerm.getTermIds()){
            list.add(TermSimpleDto.builder().id(id).build());
        }

        //stub
        when(dailyTermRepository.findById(any())).thenReturn(Optional.of(dailyTerm));
        when(termRepository.getTermsByIdList(any(), any())).thenReturn(list);

        //when
        List<TermSimpleDto> responseDtoList = termService.getDailyTerms(1L);
        System.out.println(responseDtoList);

        //then
        assertThat(responseDtoList.size()).isEqualTo(4);

    }

    @DisplayName("오늘의 용어를 생성하여 응답한다 - 2.존재하지 않을 때 새롭게 생성")
    @Test
    public void create_daily_term_success2_test() throws Exception{
        //given
        ArrayList<CategoryEnum> arrayList = new ArrayList<>();
        arrayList.add(CategoryEnum.IT);
        List<List<CategoryEnum>> categories = List.of(arrayList);
        List<Object[]> queryResults = List.of(new Object[] {1L, "", "", 1}, new Object[] {2L, "", "", null}, new Object[] {3L, "", "", null}, new Object[] {4L, "", "", null});

        //stub
        when(dailyTermRepository.findById(any())).thenReturn(Optional.empty());
        when(memberRepository.getCategoriesById(any())).thenReturn(categories);
        when(termRepository.getTermsByCategoriesRandom4(any(), any())).thenReturn(queryResults);

        //when
        List<TermSimpleDto> responseDtoList = termService.getDailyTerms(1L);
        System.out.println(responseDtoList);

        //then
        assertThat(responseDtoList.size()).isEqualTo(4);
        assertThat(responseDtoList.stream().map(TermSimpleDto::getId).collect(Collectors.toList())).isEqualTo(List.of(1L, 2L, 3L, 4L));
        assertThat(responseDtoList.stream().filter(dto -> dto.getId() == 1L).map(TermSimpleDto::getBookmarked).collect(Collectors.toList())).isEqualTo(List.of(BookmarkStatus.YES));
        assertThat(responseDtoList.stream().filter(dto -> dto.getId() != 1L).map(TermSimpleDto::getBookmarked).collect(Collectors.toList())).isEqualTo(List.of(BookmarkStatus.NO, BookmarkStatus.NO, BookmarkStatus.NO));

    }

    @DisplayName("오늘의 용어를 생성하여 응답한다 - 3.존재하지만 오늘 날짜가 아니다.")
    @Test
    public void create_daily_term_success3_test() throws Exception{
        //given
        DailyTerm dailyTerm = newMockDailyTerm(1L, List.of(3L, 5L, 7L, 9L));
        dailyTerm.setLastRefreshedDate(LocalDate.EPOCH);
        List<TermSimpleDto> list = new ArrayList<>();
        for(Long id: dailyTerm.getTermIds()){
            list.add(TermSimpleDto.builder().id(id).build());
        }

        ArrayList<CategoryEnum> arrayList = new ArrayList<>();
        arrayList.add(CategoryEnum.IT);
        List<List<CategoryEnum>> categories = List.of(arrayList);
        List<Object[]> queryResults = List.of(new Object[] {1L, "", "", 1}, new Object[] {2L, "", "", null}, new Object[] {3L, "", "", null}, new Object[] {4L, "", "", null});


        //stub
        when(dailyTermRepository.findById(any())).thenReturn(Optional.of(dailyTerm));
        when(memberRepository.getCategoriesById(any())).thenReturn(categories);
        when(termRepository.getTermsByCategoriesRandom4(any(), any())).thenReturn(queryResults);

        //when
        List<TermSimpleDto> responseDtoList = termService.getDailyTerms(1L);
        System.out.println(responseDtoList);

        //then
        assertThat(responseDtoList.size()).isEqualTo(4);
        assertThat(responseDtoList.stream().map(TermSimpleDto::getId).collect(Collectors.toList())).isEqualTo(List.of(1L, 2L, 3L, 4L));
        assertThat(responseDtoList.stream().filter(dto -> dto.getId() == 1L).map(TermSimpleDto::getBookmarked).collect(Collectors.toList())).isEqualTo(List.of(BookmarkStatus.YES));
        assertThat(responseDtoList.stream().filter(dto -> dto.getId() != 1L).map(TermSimpleDto::getBookmarked).collect(Collectors.toList())).isEqualTo(List.of(BookmarkStatus.NO, BookmarkStatus.NO, BookmarkStatus.NO));

    }


}