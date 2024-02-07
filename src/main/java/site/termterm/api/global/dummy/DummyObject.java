package site.termterm.api.global.dummy;

import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.dto.MemberInfoDto;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.domain.member.entity.SocialLoginType;
import site.termterm.api.domain.term.entity.Term;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DummyObject {
    protected Member newMember(String socialId, String email){
        return Member.builder()
                .socialId(socialId)
                .name("sinner")
                .email(email)
                .nickname(UUID.randomUUID().toString())
                .profileImg("image.com")
                .role(MemberEnum.CUSTOMER)
                .socialType(SocialLoginType.KAKAO)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .categories(List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS))
                .build();
    }

    protected Member newMockMember(Long id, String socialId, String email){
        return Member.builder()
                .id(id)
                .socialId(socialId)

                .name("sinner")
                .email(email)
                .profileImg("image.com")
                .nickname(UUID.randomUUID().toString())
                .role(MemberEnum.CUSTOMER)
                .socialType(SocialLoginType.KAKAO)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .categories(List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS))
                .build();
    }

    protected MemberInfoDto.BaseMemberInfoDto newMemberInfoDto(String socialId, String email){
        return MemberInfoDto.BaseMemberInfoDto.builder()
                .socialId(socialId)
                .name("sinner")
                .email(email)
                .nickname(UUID.randomUUID().toString())
                .profileImg("image.com")
                .build();
    }

    protected Term newTerm(String name, String description, List<CategoryEnum> categoryEnums){
        return Term.builder().name(name).description(description).categories(categoryEnums).build();
    }

    protected Term newMockTerm(Long id, String name, String description, List<CategoryEnum> categoryEnums){
        return Term.builder().id(id).name(name).description(description).categories(categoryEnums).build();
    }
}
