package site.termterm.api.domain.term.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.global.converter.CategoryListConverter;

import java.util.List;
import java.util.Objects;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Term {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TERM_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    @Convert(converter = CategoryListConverter.class)
    private List<CategoryEnum> categories;

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Term))
            return false;

        return this.id.longValue() == ((Term) obj).getId().longValue();
    }
}
