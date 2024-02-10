package site.termterm.api.domain.folder.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.global.converter.LongListConverter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class Folder {
    private static final int FOLDER_LIMIT = 50;

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "FOLDER_ID")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Builder.Default
    private Integer saveLimit = FOLDER_LIMIT;  // 추후 포인트 지불 시 폴더 한도 증가를 위해

    @Convert(converter = LongListConverter.class)
    @Builder.Default
    private List<Long> termIds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    public Folder modifyInfo(String title, String description){
        this.title = title;
        this.description = description;

        return this;
    }

}
