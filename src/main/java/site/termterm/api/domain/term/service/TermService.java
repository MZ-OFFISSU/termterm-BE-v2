package site.termterm.api.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

import site.termterm.api.domain.term.repository.TermRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {
    private final TermRepository termRepository;

    public List<TermIdAndNameAndBookmarkStatusResponseDto> searchTerm(String name) {

        return termRepository.getSearchResults(name);
    }
}
