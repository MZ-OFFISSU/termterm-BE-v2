package site.termterm.api.global.slack;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.global.exception.ResponseDto;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class SlackAspect {
    private final SlackChannels slackChannels;
    private final SlackUtil slackUtil;

    // 문의사항 등록
    @AfterReturning(
            pointcut = "execution(* site.termterm.api.domain.inquiry.service.InquiryService.registerInquiry(site.termterm.api.domain.inquiry.dto.InquiryRequestDto.InquiryRegisterRequestDto))",
            returning = "inquiry"
    )
    public void afterRegisterInquiry(Inquiry inquiry){
        String message = "[" + inquiry.getId() + "]번째 문의가 등록되었습니다. 답변 부탁드립니다.\n" + "문의 내용 : " + inquiry.getContent();
        slackUtil.sendSlackMessage(message, slackChannels.CHANNEL_VOC);
    }

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.domain.comment.service.CommentService.registerComment(site.termterm.api.domain.comment.dto.CommentRequestDto.CommentRegisterRequestDto, Long))",
            returning = "comment"
    )
    public void afterRegisterComment(Comment comment){
        String message = "[" + comment.getTermId() + "] 용어의 '나만의 용어설명'이 등록되었습니다. 검토 부탁드립니다.";
        slackUtil.sendSlackMessage(message, slackChannels.CHANNEL_VOC);
    }

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.domain.comment.service.CommentService.receiveReport(site.termterm.api.domain.comment.domain.report.dto.ReportRequestDto.ReportSubmitRequestDto, Long))",
            returning = "report"
    )
    public void afterReportComment(Report report){
        String message = "[" + report.getComment().getTermId() + "] 용어의 '나만의 용어설명'의 신고가 접수되었습니다. 해당 용어설명은 일시적으로 블락되었으니 어서 검토 부탁드립니다.";
        slackUtil.sendSlackMessage(message, slackChannels.CHANNEL_VOC);
    }

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.domain.point.service.PointService.payForCuration(Long, Long))",
            returning = "curation"
    )
    public void afterPaidCuration(Curation curation){
        String message = "큐레이션 구매! - " + curation.getTitle();
        slackUtil.sendSlackMessage(message, slackChannels.CHANNEL_INFO);
    }

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.domain.point.service..PointService.payForFolder(Long))"
    )
    public void afterPurchaseFolder(){
        String message = "폴더 구매!";
        slackUtil.sendSlackMessage(message, slackChannels.CHANNEL_INFO);
    }

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.global.handler.exceptions.CustomExceptionHandler.validationApiException(site.termterm.api.global.handler.exceptions.CustomValidationException))",
            returning = "response"
    )
    public void afterOccurValidationError(ResponseEntity<ResponseDto<?>> response){
        String message = Objects.requireNonNull(response.getBody()).getMessage();
        Object data = Objects.requireNonNull(response.getBody()).getData();

        slackUtil.sendSlackMessage(message + "\n" + data.toString(), slackChannels.CHANNEL_EMERGENCY);
    }

    @AfterReturning(
            pointcut = "execution(* site.termterm.api.global.handler.exceptions.CustomExceptionHandler.forbiddenException(site.termterm.api.global.handler.exceptions.CustomForbiddenException))",
            returning = "response"
    )
    public void afterOccurAuthorizationError(ResponseEntity<ResponseDto<?>> response){
        String message = Objects.requireNonNull(response.getBody()).getMessage();

        slackUtil.sendSlackMessage(message, slackChannels.CHANNEL_EMERGENCY);
    }

}
