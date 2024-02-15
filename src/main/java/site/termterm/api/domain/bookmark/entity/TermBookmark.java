package site.termterm.api.domain.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.bookmark.composite_id.TermBookmarkId;
import site.termterm.api.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@IdClass(TermBookmarkId.class)
public class TermBookmark {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Id
    private Long termId;

    @Builder.Default
    private Integer folderCnt = 1;

    public void addFolderCnt(int plus){
        this.folderCnt += plus;
    }

    public static TermBookmark of(Long termId, Member member, int folderCnt){
        return TermBookmark.builder().termId(termId).member(member).folderCnt(folderCnt).build();
    }

}
