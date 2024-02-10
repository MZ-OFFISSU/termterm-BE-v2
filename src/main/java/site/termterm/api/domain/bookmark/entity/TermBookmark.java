package site.termterm.api.domain.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TermBookmark {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "TERM_BOOKMARK_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERM_ID")
    private Term term;

    @Builder.Default
    private Integer folderCnt = 1;

    public void addFolderCnt(int plus){
        this.folderCnt += plus;
    }

    public static TermBookmark of(Term term, Member member, int folderCnt){
        return TermBookmark.builder().term(term).member(member).folderCnt(folderCnt).build();
    }

}
