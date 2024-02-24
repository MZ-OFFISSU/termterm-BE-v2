package site.termterm.api.domain.home_title.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.home_title.entity.HomeSubtitle;

public class HomeTitleRequestDto {

    @Getter
    @Setter
    public static class HomeSubtitleRegisterRequestDto {

        @NotBlank
        @Size(max = 50)
        private String subtitle;

        public HomeSubtitle toEntity(){
            return new HomeSubtitle(subtitle);
        }
    }
}
