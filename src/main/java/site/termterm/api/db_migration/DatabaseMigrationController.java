package site.termterm.api.db_migration;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;
import static site.termterm.api.db_migration.MigrationRequestDto.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/admin/v1-to-v2-db-migration")
@Hidden
public class DatabaseMigrationController {
    private final DatabaseMigrationService migrationService;

    @PostMapping("/member")
    public ResponseEntity<ResponseDto<?>> migMem(@RequestBody MemberRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateMember(requestDto.getMemberDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "성공", null), HttpStatus.OK);
    }

    @PostMapping("/term")
    public ResponseEntity<ResponseDto<?>> migTerm(@RequestBody TermRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateTerm(requestDto.getTermDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "성공", null), HttpStatus.OK);
    }

    @PostMapping("/term-bookmark")
    public ResponseEntity<ResponseDto<?>> migTermBookmark(@RequestBody TermBookmarkRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateTermBookmark(requestDto.getTermBookmarkList());

        return new ResponseEntity<>(new ResponseDto<>(1, "성공", null), HttpStatus.OK);
    }

    @PostMapping("/folder")
    public ResponseEntity<ResponseDto<?>> migFolder(@RequestBody FolderRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateFolder(requestDto.getFolderDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "성공", null), HttpStatus.OK);
    }

    @PostMapping("/curation")
    public ResponseEntity<ResponseDto<?>> migCuration(@RequestBody CurationRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateCuration(requestDto.getCurationDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @PostMapping("/comment")
    public ResponseEntity<ResponseDto<?>> migComment(@RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateComment(requestDto.getCommentDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @PostMapping("/curation-bookmark")
    public ResponseEntity<ResponseDto<?>> migCurationBookmark(@RequestBody CurationBookmarkRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateCurationBookmark(requestDto.getCurationBookmarkDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @PostMapping("/curation-paid")
    public ResponseEntity<ResponseDto<?>> migCurationPaid(@RequestBody CurationPaidRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateCurationPaid(requestDto.getCurationPaidDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @PostMapping("/subtitle")
    public ResponseEntity<ResponseDto<?>> migSubtitle(@RequestBody SubtitleRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migrateSubtitle(requestDto.getSubtitleDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @PostMapping("/point-history")
    public ResponseEntity<ResponseDto<?>> migPointHistory(@RequestBody PointHistoryRequestDto requestDto, @AuthenticationPrincipal LoginMember loginMember){
        migrationService.migratePointHistory(requestDto.getPointHistoryDtoList());

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }
}
