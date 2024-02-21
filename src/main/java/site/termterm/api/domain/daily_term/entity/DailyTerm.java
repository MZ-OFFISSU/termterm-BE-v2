package site.termterm.api.domain.daily_term.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.global.converter.LongListConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DailyTerm {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    @Convert(converter = LongListConverter.class)
    @Builder.Default
    private List<Long> termIds = new ArrayList<>();

    @Builder.Default
    @Setter
    private LocalDate lastRefreshedDate = LocalDate.now();

}
