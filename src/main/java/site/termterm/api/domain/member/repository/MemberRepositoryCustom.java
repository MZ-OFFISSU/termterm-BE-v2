package site.termterm.api.domain.member.repository;


import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.quiz.entity.QuizStatus;

import java.util.List;

public interface MemberRepositoryCustom {

    String getProfileImgById(Long memberId);
    List<Folder> findFoldersByMemberId(Long memberId);
    Integer findFolderLimitById(Long memberId);
    List<List<CategoryEnum>> getCategoriesById(Long memberId);
    String getIdentifierById(Long memberId);
    Integer getPointById(Long memberId);
    String getNicknameById(Long memberId);
    QuizStatus getQuizStatusById(Long memberId);
}
