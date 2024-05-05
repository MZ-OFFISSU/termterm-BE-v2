package site.termterm.api.db_migration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.curation.domain.curation_paid.repository.CurationPaidRepository;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.home_title.repository.HomeSubtitleRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;

import static site.termterm.api.db_migration.MigrationRequestDto.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DatabaseMigrationService {
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final TermBookmarkRepository termBookmarkRepository;
    private final FolderRepository folderRepository;
    private final CurationRepository curationRepository;
    private final CommentRepository commentRepository;
    private final CurationBookmarkRepository curationBookmarkRepository;
    private final CurationPaidRepository curationPaidRepository;
    private final HomeSubtitleRepository subtitleRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public void migrateMember(List<MemberDto> memberDtoList) {
        memberDtoList.forEach(memberDto -> memberRepository.save(memberDto.toEntity()));
    }

    public void migrateTerm(List<TermDto> termDtoList) {
        termDtoList.forEach(termDto -> {
            termRepository.saveWithId(termDto);
            Term termPS = termRepository.findById(termDto.getId())
                    .orElseThrow(() -> new CustomApiException(termDto.getId() + "가 저장되지 않음"));

            List<CategoryEnum> categoryEnumList = termDto.getCategories().stream().map(CategoryEnum::valueOf).toList();
            termPS.setCategories(categoryEnumList);
        });
    }

    public void migrateTermBookmark(List<TermBookmarkDto> termBookmarkList) {
        termBookmarkList.forEach(termBookmarkDto -> {
            Long memberId = termBookmarkDto.getMemberId();
            Member member = memberRepository.getReferenceById(memberId);

            int folderCnt = 0;

            List<Folder> folders = folderRepository.findFoldersByMemberId(memberId);

            for (Folder folder: folders){
                List<Long> termIds = folder.getTermIds();
                if (termIds.contains(termBookmarkDto.getTermId())){
                    folderCnt += 1;
                }
            }

            termBookmarkRepository.save(termBookmarkDto.toEntity(member, folderCnt));
        });


    }

    public void migrateFolder(List<FolderDto> folderDtoList) {
        folderDtoList.forEach(dto -> {
            folderRepository.saveWithId(dto);
            Folder folderPS = folderRepository.findById(dto.getId())
                    .orElseThrow(() -> new CustomApiException(dto.getId() + "가 저장되지 않음"));

            folderPS.setTermIds(dto.getTermIds());
        });

    }

    public void migrateCuration(List<CurationDto> curationDtoList){
        curationDtoList.forEach(curationDto -> curationRepository.save(curationDto.toEntity()));
    }

    public void migrateComment(List<CommentDto> commentDtoList) {
        commentDtoList.forEach(commentDto -> {
            Member member = memberRepository.getReferenceById(commentDto.getMemberId());

            commentRepository.save(commentDto.toEntity(member));
        });
    }

    public void migrateCurationBookmark(List<CurationBookmarkDto> curationBookmarkDtoList) {
        curationBookmarkDtoList.forEach(curationBookmarkDto -> {
            Member member = memberRepository.getReferenceById(curationBookmarkDto.getMemberId());
            Curation curation = curationRepository.getReferenceById(curationBookmarkDto.getCurationId());

            curationBookmarkRepository.save(curationBookmarkDto.toEntity(member, curation));
        });
    }

    public void migrateCurationPaid(List<CurationPaidDto> curationPaidDtoList) {
        curationPaidDtoList.forEach(curationPaidDto -> curationPaidRepository.save(curationPaidDto.toEntity()));
    }

    public void migrateSubtitle(List<SubtitleDto> subtitleDtoList) {
        subtitleDtoList.forEach(subtitleDto -> subtitleRepository.save(subtitleDto.toEntity()));
    }

    public void migratePointHistory(List<PointHistoryDto> pointHistoryDtoList) {
        pointHistoryDtoList.forEach(pointHistoryDto -> {
            Member member = memberRepository.getReferenceById(pointHistoryDto.getMemberId());

            pointHistoryRepository.save(pointHistoryDto.toEntity(member));
        });
    }
}
