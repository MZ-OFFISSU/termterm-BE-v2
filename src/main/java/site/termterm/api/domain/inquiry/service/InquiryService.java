package site.termterm.api.domain.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.domain.inquiry.repository.InquiryRepository;

import static site.termterm.api.domain.inquiry.dto.InquiryRequestDto.*;

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
}
