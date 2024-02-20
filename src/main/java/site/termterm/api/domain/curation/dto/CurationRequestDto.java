package site.termterm.api.domain.curation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.curation.entity.Curation;

import java.util.List;

public class CurationRequestDto {

    @Getter
    @Setter
    public static class CurationRegisterRequestDto {
        @NotBlank
        private String title;

        @NotBlank
        private String description;

        @NotBlank
        private String thumbnail;

        private List<@NotBlank String> tags;

        private List<@Positive Long> termIds;

        private List<@Pattern(regexp = "^(?i)(IT|BUSINESS|MARKETING|DEVELOPMENT|PM|DESIGN)$") String> categories;

        public Curation toEntity(){
            return Curation.builder()
                    .title(title)
                    .description(description)
                    .thumbnail(thumbnail)
                    .termIds(termIds)
                    .cnt(termIds.size())
                    .tags(tags)
                    .categories(categories.stream().map(categoryString -> CategoryEnum.valueOf(categoryString.toUpperCase())).toList())
                    .build();
        }
    }
}
