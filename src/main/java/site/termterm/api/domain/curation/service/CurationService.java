package site.termterm.api.domain.curation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.curation.dto.CurationRequestDto;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.repository.MemberRepository;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationService {
    private final CurationRepository curationRepository;
    private final MemberRepository memberRepository;

    /**
     * 새로운 큐레이션을 등록합니다. (for ADMIN)
     */
    public Curation register(CurationRegisterRequestDto requestDto) {

        return curationRepository.save(requestDto.toEntity());
    }
}
