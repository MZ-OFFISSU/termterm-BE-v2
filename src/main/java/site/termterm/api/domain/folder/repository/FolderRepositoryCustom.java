package site.termterm.api.domain.folder.repository;

import site.termterm.api.domain.folder.entity.Folder;

import java.util.List;

public interface FolderRepositoryCustom {
    Integer countByMemberId(Long memberId);
    List<Folder> findFoldersByMemberId(Long memberId);
}
