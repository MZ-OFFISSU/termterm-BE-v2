package site.termterm.api.domain.term.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class TermRequestDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TermListCategoryRequestDto{
        private List<@Pattern(regexp = "^(?i)(IT|BUSINESS|MARKETING|DEVELOPMENT|PM|DESIGN)$") String> categories;
    }
}
