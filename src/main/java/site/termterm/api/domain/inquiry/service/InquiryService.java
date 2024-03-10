package site.termterm.api.domain.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.domain.inquiry.entity.InquiryStatus;
import site.termterm.api.domain.inquiry.repository.InquiryRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;
import java.util.stream.Collectors;

import static site.termterm.api.domain.inquiry.dto.InquiryRequestDto.*;
import static site.termterm.api.domain.inquiry.dto.InquiryResponseDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    /**
     * 문의사항 등록
     */
    @Transactional
    public void registerInquiry(InquiryRegisterRequestDto requestDto) {
        Inquiry inquiry = requestDto.toEntity();

        inquiryRepository.save(inquiry);
    }

    /**
     * 문의사항 답변 완료 처리
     */
    @Transactional
    public void completeInquiry(Long inquiryId) {
        Inquiry inquiryPS = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomApiException("Inquiry 가 존재하지 않습니다."));

        inquiryPS.setStatus(InquiryStatus.COMPLETED);
    }

    /**
     * 문의사항 상태 대기중 변환
     */
    @Transactional
    public void waitInquiry(Long inquiryId) {
        Inquiry inquiryPS = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomApiException("Inquiry 가 존재하지 않습니다."));

        inquiryPS.setStatus(InquiryStatus.WAITING);
    }

    /**
     * 전체 문의사항 리스트 조회
     */
    public List<InquiryInfoDto> getEntireInquiryList() {
        List<Inquiry> all = inquiryRepository.findAll();

        return all.stream().map(InquiryInfoDto::from).collect(Collectors.toList());
    }
}
