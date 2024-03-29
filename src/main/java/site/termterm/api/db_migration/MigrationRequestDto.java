package site.termterm.api.db_migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.home_title.entity.HomeSubtitle;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.domain.member.entity.SocialLoginType;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.entity.Sign;
import site.termterm.api.domain.quiz.entity.QuizStatus;
import site.termterm.api.domain.term.entity.Term;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MigrationRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRequestDto{
        private List<MemberDto> memberDtoList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto{
        private Long id;
        private String socialId;
        private String name;
        private String email;
        private String profileImg;
        private String nickname;
        private String introduction;
        private String job;
        private Integer yearCareer;
        private String domain;
        private Integer point;
        private String socialType;
        private String identifier;
        private String refreshToken;
        private String quizStatus;
        private Integer folderLimit;
        private Integer currentFolderCount;

        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private List<String> categories;
        
        public Member toEntity(){
            if (socialType.equals("WITHDRAWED"))
                socialType = "WITHDRAWN";

            if (socialId == null)
                socialId = UUID.randomUUID().toString();

            if (name == null)
                name = UUID.randomUUID().toString();

            if (nickname == null)
                nickname = UUID.randomUUID().toString();

            if (email == null)
                email = "null@null.com";

            if (createdDate == null)
                createdDate = LocalDateTime.of(LocalDate.EPOCH, LocalTime.now());
            if (modifiedDate == null)
                modifiedDate = LocalDateTime.of(LocalDate.EPOCH, LocalTime.now());

            List<CategoryEnum> categoryEnumList = categories.stream().map(CategoryEnum::valueOf).toList();

            return Member.builder()
                    .id(id)
                    .socialId(socialId)
                    .name(name)
                    .email(email)
                    .profileImg(profileImg)
                    .nickname(nickname)
                    .introduction(introduction)
                    .job(job)
                    .yearCareer(yearCareer)
                    .domain(domain)
                    .socialType(SocialLoginType.valueOf(socialType))
                    .identifier(identifier)
                    .quizStatus(QuizStatus.valueOf(quizStatus))
                    .point(point)
                    .folderLimit(folderLimit)
                    .createdDate(createdDate)
                    .modifiedDate(modifiedDate)
                    .role(MemberEnum.CUSTOMER)
                    .categories(categoryEnumList)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermRequestDto{
        private List<TermDto> termDtoList;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermDto {
        private Long id;
        private String name;
        private String description;
        private List<String> categories;

        public Term toEntity(){
            List<CategoryEnum> categoryEnumList = categories.stream().map(CategoryEnum::valueOf).toList();

            return Term.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .categories(categoryEnumList)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class TermBookmarkDto{
        private Long memberId;
        private Long termId;

        public TermBookmark toEntity(Member member, Integer folderCnt){
            return TermBookmark.builder()
                    .member(member)
                    .termId(termId)
                    .folderCnt(folderCnt)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermBookmarkRequestDto{
        List<TermBookmarkDto> termBookmarkList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolderDto{
        private Long id;
        private Long memberId;
        private String title;
        private String description;
        private Integer saveLimit;
        private List<Long> termIds;

        public Folder toEntity(Member member){
            return Folder.builder()
                    .id(id)
                    .member(member)
                    .title(title)
                    .description(description)
                    .saveLimit(saveLimit)
                    .termIds(termIds)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FolderRequestDto{
        List<FolderDto> folderDtoList;
    }

    @Getter
    @Setter
    public static class CurationDto{
        private Long id;
        private Integer cnt;
        private String description;
        private String thumbnail;
        private String title;
        private List<Long> termIds;
        private List<String> tags;
        private List<String> categories;

        public Curation toEntity(){
            List<CategoryEnum> categoryEnumList = categories.stream().map(CategoryEnum::valueOf).collect(Collectors.toList());

            return Curation.builder()
                    .id(id)
                    .cnt(cnt)
                    .description(description)
                    .thumbnail(thumbnail)
                    .title(title)
                    .termIds(termIds)
                    .tags(tags)
                    .categories(categoryEnumList)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurationRequestDto{
        private List<CurationDto> curationDtoList;
    }

    @Getter
    @Setter
    public static class CommentDto{
        private Long id;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private String content;
        private Integer likeCnt;
        private String source;
        private String status;
        private Long memberId;
        private Long termId;

        public Comment toEntity(Member member){
            return Comment.builder()
                    .id(id)
                    .createdDate(createdDate)
                    .modifiedDate(modifiedDate)
                    .content(content)
                    .likeCnt(likeCnt)
                    .source(source)
                    .status(CommentStatus.valueOf(status))
                    .member(member)
                    .termId(termId)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentRequestDto{
        List<CommentDto> commentDtoList;
    }

    @Getter
    @Setter
    public static class CurationBookmarkDto{
        private String status;
        private Long curationId;
        private Long memberId;

        public CurationBookmark toEntity(Member member, Curation curation){
            return CurationBookmark.builder()
                    .status(BookmarkStatus.valueOf(status))
                    .member(member)
                    .curation(curation)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurationBookmarkRequestDto{
        List<CurationBookmarkDto> curationBookmarkDtoList;
    }

    @Getter
    @Setter
    public static class CurationPaidDto{
        private Long memberId;
        private LocalDateTime modifiedDate;
        private List<Long> curationIds;

        public CurationPaid toEntity(){
            return CurationPaid.builder()
                    .modifiedDate(modifiedDate)
                    .id(memberId)
                    .curationIds(curationIds)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurationPaidRequestDto{
        List<CurationPaidDto> curationPaidDtoList;
    }

    @Getter
    @Setter
    public static class SubtitleDto{
        private String subtitle;

        public HomeSubtitle toEntity(){
            return new HomeSubtitle(subtitle);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubtitleRequestDto{
        List<SubtitleDto> subtitleDtoList;
    }

    @Getter
    @Setter
    public static class PointHistoryDto{
        private LocalDate date;
        private Integer val;
        private Long memberId;
        private String detail;
        private String subText;
        private String sign;

        public PointHistory toEntity(Member member){
            if (sign.equals("+"))
                sign = "PLUS";
            else
                sign = "MINUS";

            return PointHistory.builder()
                    .date(date)
                    .value(val)
                    .member(member)
                    .detail(detail)
                    .subText(subText)
                    .sign(Sign.valueOf(sign))
                    .memberPoint(member.getPoint())
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PointHistoryRequestDto{
        List<PointHistoryDto> pointHistoryDtoList;
    }
}
