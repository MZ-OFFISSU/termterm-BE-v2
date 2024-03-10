package site.termterm.api.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.repository.ReportRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import static site.termterm.api.domain.comment.dto.ReportResponseDto.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final TermRepository termRepository;

    /**
     * 신고된 나만의 용어설명 리스트 리턴
     */
    public List<ReportInfoForAdminDto> getReportedCommentList() {
        List<Report> all = reportRepository.findAllWithComment();

        return all.stream().map(r -> {
            Term term = termRepository.findById(r.getComment().getId())
                    .orElseThrow(() -> new CustomApiException("존재하지 않는 용어입니다."));

            return ReportInfoForAdminDto.from(r, term.getName());
        }).collect(Collectors.toList());

    }

    /**
     * 신고 내역 처리 완료 (ADMIN)
     */
    @Transactional
    public void completeReport(Long reportId) {
        Report reportPS = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomApiException("신고 내용이 존재하지 않습니다."));

        reportPS.completeReport();
    }
}
