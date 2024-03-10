package site.termterm.api.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppleRefreshToken {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String token;
}
