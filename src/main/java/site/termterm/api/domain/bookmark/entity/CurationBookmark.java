package site.termterm.api.domain.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.bookmark.composite_id.CurationBookmarkId;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@IdClass(CurationBookmarkId.class)
@AllArgsConstructor
@Builder
public class CurationBookmark {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURATION_ID")
    private Curation curation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookmarkStatus status = BookmarkStatus.YES;

    public static CurationBookmark of(Curation curation, Member member){
        return CurationBookmark.builder().curation(curation).member(member).build();
    }

    //TODO : Cron 으로 매일 자정 NO 인 튜플 삭제
    public CurationBookmark setStatus(BookmarkStatus status){
        this.status = status;
        return this;
    }
}
