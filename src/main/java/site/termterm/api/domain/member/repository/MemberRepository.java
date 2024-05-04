package site.termterm.api.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findBySocialIdAndEmail(@Param("socialId") String socialId, @Param("email") String email);
    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);
    Boolean existsByNicknameIgnoreCase(@Param("nickname") String nickname);
}
