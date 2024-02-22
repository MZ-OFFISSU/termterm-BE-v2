package site.termterm.api.domain.inquiry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.domain.inquiry.entity.InquiryType;

public class InquiryRequestDto {
    @Getter
    @Setter
    public static class InquiryRegisterRequestDto {
        @Email
        private String email;

        @Pattern(regexp = "^(?i)(USE|AUTH|REPORT|SUGGESTION|OTHER)$")
        private String type;

        @NotBlank
        @Size(max = 1000)
        private String content;

        public Inquiry toEntity(){
            return Inquiry.builder()
                    .email(email)
                    .content(content)
                    .type(InquiryType.valueOf(type))
                    .build();
        }

    }

}
