package site.termterm.api.domain.home_title.dto;

import lombok.*;

public class HomeTitleResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class HomeTitleByMemberResponseDto {
        private String mainTitle;
        private String subTitle;

        public static HomeTitleByMemberResponseDto of(String mainTitle, String subTitle){
            return HomeTitleByMemberResponseDto.builder().mainTitle(mainTitle).subTitle(subTitle).build();
        }
    }
}
