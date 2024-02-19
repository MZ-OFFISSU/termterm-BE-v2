package site.termterm.api.domain.curation.domain.curation_paid.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import site.termterm.api.global.converter.LongListConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurationPaid {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    @Convert(converter = LongListConverter.class)
    @Builder.Default
    private List<Long> curationIds = new ArrayList<>();

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;
}
