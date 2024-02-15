package site.termterm.api.domain.curation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.global.converter.CategoryListConverter;
import site.termterm.api.global.converter.LongListConverter;
import site.termterm.api.global.converter.StringListConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
@Getter
@ToString
public class Curation {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "CURATION_ID")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer cnt;

    @Column(nullable = false)
    private String thumbnail;

    @Convert(converter = LongListConverter.class)
    @Builder.Default
    private List<Long> termIds = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    private List<String> tags;

    @Convert(converter = CategoryListConverter.class)
    private List<CategoryEnum> categories;

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate   // Insert, Update
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    public Curation syncCnt(){
        this.cnt = this.termIds.size();

        return this;
    }
}
