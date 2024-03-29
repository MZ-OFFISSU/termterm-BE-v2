package site.termterm.api.domain.inquiry.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.global.exception.ResponseDto;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
@Profile("default")
public class MailAspect {
    private final MailSendUtil mailSendUtil;

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.domain.inquiry.controller.InquiryController.registerInquiry(site.termterm.api.domain.inquiry.dto.InquiryRequestDto.InquiryRegisterRequestDto, org.springframework.validation.BindingResult))",
            returning = "returned"
    )
    public void afterReturnRegisterInquiry(ResponseEntity<ResponseDto<Inquiry>> returned){
        Inquiry inquiry = Objects.requireNonNull(returned.getBody()).getData();
        mailSendUtil.sendAcceptMail(inquiry.getEmail());
    }
}
