package site.termterm.api.global.dummy;

import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.entity.ReportStatus;
import site.termterm.api.domain.comment.domain.report.entity.ReportType;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment_like.entity.CommentLike;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.daily_term.entity.DailyTerm;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.member.dto.MemberInfoDto;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.domain.member.entity.SocialLoginType;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.term.entity.Term;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DummyObject {
    protected Member newMember(String socialId, String email){
        return Member.builder()
                .socialId(socialId)
                .name("sinner")
                .email(email)
                .nickname(UUID.randomUUID().toString())
                .profileImg("image.com")
                .role(MemberEnum.CUSTOMER)
                .socialType(SocialLoginType.KAKAO)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .categories(List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS))
                .build();
    }

    protected Member newAdmin(){
        return Member.builder()
                .socialId("admin")
                .name("admin")
                .email("admin@admin.com")
                .nickname(UUID.randomUUID().toString())
                .profileImg("image.com")
                .role(MemberEnum.ADMIN)
                .socialType(SocialLoginType.KAKAO)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .categories(List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS))
                .build();
    }

    protected Member newMockMember(Long id, String socialId, String email){
        return Member.builder()
                .id(id)
                .socialId(socialId)
                .name("sinner")
                .email(email)
                .profileImg("image.com")
                .nickname(UUID.randomUUID().toString())
                .role(MemberEnum.CUSTOMER)
                .socialType(SocialLoginType.KAKAO)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .categories(List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS))
                .build();
    }

    protected MemberInfoDto.BaseMemberInfoDto newMemberInfoDto(String socialId, String email){
        return MemberInfoDto.BaseMemberInfoDto.builder()
                .socialId(socialId)
                .name("sinner")
                .email(email)
                .nickname(UUID.randomUUID().toString())
                .profileImg("image.com")
                .build();
    }

    protected Term newTerm(String name, String description, List<CategoryEnum> categoryEnums){
        return Term.builder().name(name).description(description).categories(categoryEnums).build();
    }

    protected Term newMockTerm(Long id, String name, String description, List<CategoryEnum> categoryEnums){
        return Term.builder().id(id).name(name).description(description).categories(categoryEnums).build();
    }

    protected Folder newFolder(String title, String description, Member member){
        return Folder.builder().title(title).description(description).member(member).build();
    }

    protected Folder newMockFolder(Long id, String title, String description, Member member){
        return Folder.builder().id(id).title(title).description(description).member(member).build();
    }

    protected TermBookmark newTermBookmark(Term term, Member member, int folderCnt){
        return TermBookmark.of(term.getId(), member, folderCnt);
    }

    protected Comment newComment(String content, String source, Member member, Term term){
        return Comment.builder()
                .content(content)
                .source(source)
                .member(member)
                .termId(term.getId())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    protected Comment newMockComment(Long id, String content, String source, Term term, Member member){
        return Comment.builder()
                .id(id)
                .content(content)
                .source(source)
                .member(member)
                .termId(term.getId())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    protected CommentLike newMockCommentLike(Comment comment, Member member, CommentLikeStatus status){
        return CommentLike.builder().comment(comment).member(member).status(status).build();
    }

    protected Report newReport(String content, ReportType reportType, ReportStatus reportStatus, Comment comment, Member member){
        return Report.builder()
                .content(content)
                .type(reportType)
                .status(reportStatus)
                .comment(comment)
                .member(member)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    protected Report newMockReport(Long id, String content, ReportType reportType, ReportStatus reportStatus, Comment comment, Member member){
        return Report.builder()
                .id(id)
                .content(content)
                .type(reportType)
                .status(reportStatus)
                .comment(comment)
                .member(member)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    protected Curation newCuration(String title, List<Long> termIds, List<String> tags, List<CategoryEnum> categories){
        return Curation.builder()
                .title(title)
                .description("큐레이션 설명입니다.")
                .cnt(termIds.size())
                .thumbnail("google.com")
                .termIds(termIds)
                .tags(tags)
                .categories(categories)
                .build();
    }

    protected Curation newMockCuration(Long id, String title, List<Long> termIds, List<String> tags, List<CategoryEnum> categories){
        return Curation.builder()
                .id(id)
                .title(title)
                .description("큐레이션 설명입니다.")
                .cnt(termIds.size())
                .thumbnail("google.com")
                .termIds(termIds)
                .tags(tags)
                .categories(categories)
                .build();
    }

    protected CurationBookmark newCurationBookmark(Curation curation, Member member){
        return CurationBookmark.of(curation, member);
    }


    protected CurationPaid newCurationPaid(Member member, List<Long> curationIds){
        return CurationPaid.builder()
                .id(member.getId()).curationIds(curationIds).modifiedDate(LocalDateTime.now()).build();
    }

    protected DailyTerm newMockDailyTerm(Long memberId, List<Long> termIds){
        return DailyTerm.builder().id(memberId).termIds(termIds).build();
    }

    protected PointHistory newPointHistory(PointPaidType type, Member member, Integer beforePoint){
        return PointHistory.of(type, member, beforePoint);
    }

}
