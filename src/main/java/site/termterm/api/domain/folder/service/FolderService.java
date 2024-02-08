package site.termterm.api.domain.folder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Objects;

import static site.termterm.api.domain.folder.dto.FolderRequestDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;

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
        Folder folder = folderRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> new CustomApiException("폴더가 존재하지 않습니다."));

        if (folder.getMember().getId().longValue() != id.longValue()){
            throw new CustomApiException("폴더의 소유자가 로그인한 사용자가 아닙니다.");
        }

        return folder.modifyInfo(requestDto.getName(), requestDto.getDescription());
    }
}
