package site.termterm.api.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;
import static site.termterm.api.domain.term.dto.TermRequestDto.*;

import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {
    private final TermRepository termRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    /**
     * 용어 검색
     */
    public List<TermIdAndNameAndBookmarkStatusResponseDto> searchTerm(String name, Long memberId) {

        return termRepository.getSearchResults(name, memberId);
    }

    /**
     * 용어 상세
     */
    public TermDetailDto getTermDetail(Long termId, Long memberId) {
        TermDetailDto responseDto = termRepository.getTermDetailDto(termId, memberId);
        List<TermDetailDto.CommentDto> commentDtoList = commentRepository.getCommentDtoByTermIdAndMemberId(termId, memberId, CommentStatus.ACCEPTED, CommentStatus.REPORTED);
        responseDto.setComments(commentDtoList);

        return responseDto;
    }

    /**
     * 전체 용어 리스트 - Paging
     * category 가 존재하지 않을 경우 추천 단어 리스트
     */
    public Page<TermSimpleDto> getRecommendedTerms(Pageable pageable, Long memberId) {
        // 사용자의 관심사를 불러온다.
        List<ArrayList<CategoryEnum>> categoryEnumListOfList = memberRepository.getCategoriesById(memberId);

        if (categoryEnumListOfList.isEmpty()){
            throw new CustomApiException("Member Category 가 존재하지 않습니다.");
        }

        List<CategoryEnum> categoryList = categoryEnumListOfList.get(0);

        // 사용자의 관심사 카테고리에 해당하는 단어들을 불러온다.
        List<Object[]> queryResults = termRepository.getTermsByCategories(categoryList, memberId);
        List<TermSimpleDto> termDtoList = queryResults.stream().map(TermSimpleDto::of).collect(Collectors.toList());

        // paging 처리
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), termDtoList.size());

        return new PageImpl<>(termDtoList.subList(start, end), pageable, termDtoList.size());
    }

    /**
     * 전체 용어 리스트 - Paging
     * 요청 바디에 카테고리가 존재할 경우
     */
    public Page<TermSimpleDto> getTermListByCategories(TermListCategoryRequestDto requestDto, Pageable pageable, Long memberId) {
        List<CategoryEnum> categoryList = requestDto.getCategories().stream().map(CategoryEnum::valueOf).toList();

        // 사용자의 관심사 카테고리에 해당하는 단어들을 불러온다.
        List<Object[]> queryResults = termRepository.getTermsByCategories(categoryList, memberId);
        List<TermSimpleDto> termDtoList = queryResults.stream().map(TermSimpleDto::of).collect(Collectors.toList());

        // paging 처리
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), termDtoList.size());

        return new PageImpl<>(termDtoList.subList(start, end), pageable, termDtoList.size());

    }
}
