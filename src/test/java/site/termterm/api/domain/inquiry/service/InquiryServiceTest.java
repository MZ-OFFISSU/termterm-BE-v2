package site.termterm.api.domain.inquiry.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
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

}