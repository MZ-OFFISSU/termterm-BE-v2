package site.termterm.api.domain.inquiry.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.inquiry.dto.InquiryResponseDto;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.domain.inquiry.entity.InquiryStatus;
import site.termterm.api.domain.inquiry.entity.InquiryType;
import site.termterm.api.domain.inquiry.repository.InquiryRepository;
import site.termterm.api.global.dummy.DummyObject;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class InquiryServiceTest extends DummyObject {

    @InjectMocks
    InquiryService inquiryService;

    @Mock
    private InquiryRepository inquiryRepository;

    @DisplayName("문의사항 처리 완료에 성공한다.")
    @Test
    public void complete_inquiry_test() throws Exception{
        //given
        Inquiry inquiry = newMockInquiry(1L, "", "", InquiryType.REPORT);

        //stub
        when(inquiryRepository.findById(any())).thenReturn(Optional.of(inquiry));

        //when
        inquiryService.completeInquiry(1L);

        //then
        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.COMPLETED);
    }

    @DisplayName("문의사항 상태를 대기중으로 설정에 성공한다.")
    @Test
    public void wait_inquiry_test() throws Exception{
        //given
        Inquiry inquiry = newMockInquiry(1L, "", "", InquiryType.REPORT).setStatus(InquiryStatus.COMPLETED);

        //stub
        when(inquiryRepository.findById(any())).thenReturn(Optional.of(inquiry));

        //when
        inquiryService.waitInquiry(1L);

        //then
        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.WAITING);
    }

    @DisplayName("문의사항 개별 조회에 성공한다.")
    @Test
    public void get_inquiry_info_test() throws Exception{
        //given
        Inquiry inquiry = newMockInquiry(1L, "em@a.il", "문의합니다.", InquiryType.AUTH).setStatus(InquiryStatus.COMPLETED);

        //stub
        when(inquiryRepository.findById(any())).thenReturn(Optional.of(inquiry));

        //when
        InquiryResponseDto.InquiryInfoDto inquiryInfo = inquiryService.getInquiryInfo(1L);

        //then
        assertThat(inquiryInfo.getId()).isEqualTo(1L);
        assertThat(inquiryInfo.getEmail()).isEqualTo("em@a.il");
        assertThat(inquiryInfo.getContent()).isEqualTo("문의합니다.");
        assertThat(inquiryInfo.getType()).isEqualTo(InquiryType.AUTH);
        assertThat(inquiryInfo.getStatus()).isEqualTo(InquiryStatus.COMPLETED);

    }


}