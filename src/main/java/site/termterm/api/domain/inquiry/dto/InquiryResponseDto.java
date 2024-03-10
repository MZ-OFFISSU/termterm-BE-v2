package site.termterm.api.domain.inquiry.dto;

import lombok.*;
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.domain.inquiry.entity.InquiryStatus;
import site.termterm.api.domain.inquiry.entity.InquiryType;

public class InquiryResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InquiryInfoDto {
        private Long id;
        private String email;
        private String date;
        private String content;
        private InquiryStatus status;
        private InquiryType type;

        public static InquiryInfoDto from(Inquiry inquiry){
            return InquiryInfoDto.builder()
                    .id(inquiry.getId())
                    .email(inquiry.getEmail())
                    .date(inquiry.getCreatedDate().toString())
                    .content(inquiry.getContent())
                    .status(inquiry.getStatus())
                    .type(inquiry.getType())
                    .build();
        }
    }

}
