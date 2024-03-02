package site.termterm.api.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.member.dto.MemberRequestDto;
import site.termterm.api.domain.quiz.entity.QuizStatus;
import site.termterm.api.global.converter.CategoryListConverter;
import site.termterm.api.global.vo.SystemVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor  // 스프링이 User 객체를 생성할 때 빈 생성자로 new 를 하기 때문에 넣어줘야 한다.
@Getter
@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
@Entity
@ToString
@DynamicUpdate
@DynamicInsert
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profileImg;

    @Column(nullable = false, unique = true, length = 36)
    private String nickname;

    @Column(length = 100)
    private String introduction;


    @Column(length = 20)
    private String job;

    private Integer yearCareer;
    @Column(length = 20)
    private String domain;

    @Builder.Default
    @Column(nullable = false)
    private Integer point = 500;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialLoginType socialType;

    @Builder.Default
    private Integer folderLimit = 3;

    @Builder.Default
    private String identifier = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private QuizStatus quizStatus = QuizStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberEnum role;  // ADMIN, CUSTOMER

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate   // Insert, Update
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @Builder.Default
    @Setter
    private String refreshToken = UUID.randomUUID().toString();

    @Convert(converter = CategoryListConverter.class)
    private List<CategoryEnum> categories;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TermBookmark> termBookmarks;

    @Builder
    public Member(Long id, String socialId, String name, String email, String profileImg, String nickname,List<CategoryEnum> categories,  MemberEnum role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.role = role;
        this.createdDate = createdAt;
        this.modifiedDate = updatedAt;
        this.categories = categories;
    }

    public Member updateInfo(MemberRequestDto.MemberInfoUpdateRequestDto requestDto){
        this.nickname = requestDto.getNickname();
        this.domain = requestDto.getDomain();
        this.job = requestDto.getJob();
        this.yearCareer = requestDto.getYearCareer();
        this.introduction = requestDto.getIntroduction();

        return this;
    }

    public Member updateCategories(List<CategoryEnum> categories){
        this.categories = categories;

        return this;
    }

    public Member withdraw(){
        this.email = "withdrawn@with.draw";
        this.domain = null;
        this.introduction = null;
        this.name = "withdrawn";
        this.nickname = this.identifier;
        this.socialId = "withdrawn";
        this.socialType = SocialLoginType.WITHDRAWN;
        this.yearCareer = 0;
        this.categories = null;

        return this;
    }

    public Member updateProfileImg(String url) {
        this.profileImg = url;

        return this;
    }

    public Member addFolderLimit() {
        if (this.getFolderLimit() >= SystemVO.SYSTEM_FOLDER_LIMIT){
            throw new RuntimeException();
        }

        this.folderLimit++;
        return this;
    }

    public Member setPoint(Integer point){
        this.point = point;

        return this;
    }

    public Member addPoint(Integer point){
        this.point += point;
        return this;
    }

    public Member setFolderLimit(Integer limit){
        if (limit > SystemVO.SYSTEM_FOLDER_LIMIT){
            throw new RuntimeException();
        }

        this.folderLimit = limit;

        return this;
    }

    public Member setQuizStatus(QuizStatus status){
        this.quizStatus = status;
        return this;
    }


}
