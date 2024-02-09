package site.termterm.api.domain.term.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.global.converter.CategoryListConverter;

import java.util.List;

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

    @OneToMany(mappedBy = "term")
    private List<TermBookmark> termBookmarks;

}
