package site.termterm.api.domain.folder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static site.termterm.api.domain.folder.dto.FolderRequestDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final TermBookmarkRepository termBookmarkRepository;

    /**
     * 새로운 폴더를 생성합니다.
     */
    @Transactional
    public FolderCreateResponseDto createNewFolder(FolderCreateRequestDto requestDto, Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        if (Objects.equals(memberPS.getFolders().size(), memberPS.getFolderLimit())){
            throw new CustomApiException("생성 가능한 폴더의 개수를 초과하였습니다.");
        }

        Folder newFolder = requestDto.toEntity(memberPS);
        folderRepository.save(newFolder);

        return new FolderCreateResponseDto(newFolder.getId(), newFolder.getTitle());
    }

    /**
     * 폴더 정보를 수정합니다.
     */
    @Transactional
    public Folder modifyFolderInfo(FolderModifyRequestDto requestDto, Long id) {
        Folder folderPS = folderRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> new CustomApiException("폴더가 존재하지 않습니다."));

        if (folderPS.getMember().getId().longValue() != id.longValue()){
            throw new CustomApiException("폴더의 소유자가 로그인한 사용자가 아닙니다.");
        }

        return folderPS.modifyInfo(requestDto.getName(), requestDto.getDescription());
    }

    /**
     * 폴더를 삭제합니다.
     */
    @Transactional
    public void deleteFolder(Long folderId, Long memberId) {
        Folder folderPS = folderRepository.findById(folderId)
                .orElseThrow(() -> new CustomApiException("폴더가 존재하지 않습니다."));

        if (folderPS.getMember().getId().longValue() != memberId.longValue()){
            throw new CustomApiException("폴더의 소유자가 로그인한 사용자가 아닙니다.");
        }

        Member memberPS = memberRepository.getReferenceById(memberId);

        for(Long termId: folderPS.getTermIds()){
            Term termPS = termRepository.getReferenceById(termId);

            TermBookmark termBookmarkPS = termBookmarkRepository.findByTermAndMember(termPS, memberPS)
                    .orElseThrow(() -> new CustomApiException("북마크 기록이 존재하지 않습니다. 데이터 동기화가 잘못 되었습니다.", "Folder : " + folderPS.getId() + "\nTerm : " + termId + "\nMember : " + memberId));

            if (termBookmarkPS.getFolderCnt() <= 1){        // 현재 이 용어는 하나의 폴더에만 속해 있으므로, 그대로 삭제하면 된다.
                termBookmarkRepository.delete(termBookmarkPS);
            }else{
                termBookmarkPS.addFolderCnt(-1);
            }
        }

        folderRepository.delete(folderPS);
        memberPS.getFolders().remove(folderPS);
    }

    /**
     * 폴더에 용어를 저장합니다. (아카이빙)
     * 한 번에 여러 폴더에 저장할 수 있습니다.
     * 그러나 폴더에 이미 해당 용어가 담겨 있을 경우, 이미 담은 폴더명들을 리스트로 반환합니다.
     */
    @Transactional
    public TermBookmark archiveTerm(ArchiveTermRequestDto requestDto, Long memberId) {
        List<String> alreadyArchivedFolderNames = new ArrayList<>();

        for (Long folderId: requestDto.getFolderIds()){
            Folder folderPS = folderRepository.findById(folderId).orElseThrow(() -> new CustomApiException("폴더가 존재하지 않습니다."));

            if (folderPS.getMember().getId().longValue() != memberId.longValue()) {
                throw new CustomApiException("폴더의 소유자가 로그인한 사용자가 아닙니다.");
            }

            if (folderPS.getTermIds().size() >= folderPS.getSaveLimit()){
                throw new CustomApiException("폴더가 다 찼습니다.");
            }

            if (folderPS.getTermIds().contains(requestDto.getTermId())){
                alreadyArchivedFolderNames.add(folderPS.getTitle());
            }

            folderPS.getTermIds().add(requestDto.getTermId());
        }

        // 선택한 폴더 중 하나의 폴더라도 해당 단어를 기저장하고 있을 경우, 해당 폴더 명과 함께 저장 로직 롤백
        if (!alreadyArchivedFolderNames.isEmpty()){
            throw new CustomApiException("아래 폴더들에 이미 저장된 용어입니다.", alreadyArchivedFolderNames);
        }

        Term termPS = termRepository.getReferenceById(requestDto.getTermId());
        Member memberPS = memberRepository.getReferenceById(memberId);

        // 북마크 테이블을 업데이트 합니다.
        Optional<TermBookmark> termBookmarkOptional = termBookmarkRepository.findByTermAndMember(termPS, memberPS);

        if (termBookmarkOptional.isEmpty()){
            try {
                return termBookmarkRepository.save(TermBookmark.of(termPS, memberPS, requestDto.getFolderIds().size()));
            }catch (DataIntegrityViolationException e){
                throw new CustomApiException("용어/사용자 가 존재하지 않습니다.");
            }
        }else{
            termBookmarkOptional.get().addFolderCnt(requestDto.getFolderIds().size());
            return termBookmarkOptional.get();
        }

    }

    /**
     * 폴더에서 용어를 삭제합니다.
     */
    @Transactional
    public Folder unArchiveTerm(UnArchiveTermRequestDto requestDto, Long memberId) {
        // 폴더 소유자 확인
        Folder folderPS = folderRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> new CustomApiException("폴더가 존재하지 않습니다."));

        // TermBookmark 객체의 folderCnt -= 1,  folderCnt 가 0 이 되었을 경우 삭제
        Term termPS = termRepository.getReferenceById(requestDto.getTermId());
        Member memberPS = memberRepository.getReferenceById(memberId);

        TermBookmark termBookmarkPS = termBookmarkRepository.findByTermAndMember(termPS, memberPS)
                .orElseThrow(() -> new CustomApiException("북마크 이력이 존재하지 않습니다."));

        termBookmarkPS.addFolderCnt(-1);
        if (termBookmarkPS.getFolderCnt() == 0){
            termBookmarkRepository.delete(termBookmarkPS);
        }

        // 폴더에서 용어 삭제
        folderPS.getTermIds().remove(requestDto.getTermId());

        return folderPS;
    }
}
