package site.termterm.api.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;

    public List<TermIdAndNameAndBookmarkStatusResponseDto> searchTerm(String name, Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        return termRepository.getSearchResults(name);
    }
}
