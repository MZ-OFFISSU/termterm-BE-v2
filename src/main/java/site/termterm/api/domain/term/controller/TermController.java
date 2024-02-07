package site.termterm.api.domain.term.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.termterm.api.domain.term.service.TermService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import java.util.List;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class TermController {
    private final TermService termService;

    /**
     * 용어 검색
     */
    @GetMapping("/s/term/search/{name}")
    public ResponseEntity<ResponseDto<List<TermIdAndNameAndBookmarkStatusResponseDto>>> searchTerm(@PathVariable(value = "name") String name, @AuthenticationPrincipal LoginMember loginMember){
        List<TermIdAndNameAndBookmarkStatusResponseDto> responseDtos = termService.searchTerm(name, loginMember.getMember().getId());

        if (responseDtos.isEmpty()){
            return new ResponseEntity<>(new ResponseDto<>(-1, "검색 결과가 존재하지 않습니다.", responseDtos), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "용어 검색에 성공하였습니다.", responseDtos), HttpStatus.OK);

    }
}
