package site.termterm.api.domain.point.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.member.entity.Member;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PointHistory {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "POINT_HISTORY_ID")
    private Long id;

    @Builder.Default
    private LocalDate date = LocalDate.now();

    private String detail;

    @Enumerated(EnumType.STRING)
    private Sign sign;

    @Column(name = "val")
    private Integer value;

    private Integer memberPoint;

    private String subText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public static PointHistory of(PointPaidType type, Member member, Integer beforeMemberPoint){
        return PointHistory.builder()
                .detail(type.getDetail())
                .sign(type.getSign())
                .value(type.getPoint())
                .memberPoint(type.getSign().equals(Sign.PLUS) ? beforeMemberPoint + type.getPoint() : beforeMemberPoint - type.getPoint())
                .member(member)
                .build();
    }

    public PointHistory setSubText(String text){
        this.subText = text;
        return this;
    }

    public PointHistory setDate(LocalDate date){    // for Test
        this.date = date;
        return this;
    }

}
