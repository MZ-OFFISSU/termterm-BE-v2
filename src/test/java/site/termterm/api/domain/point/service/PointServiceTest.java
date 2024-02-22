package site.termterm.api.domain.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;
import site.termterm.api.domain.curation.domain.curation_paid.repository.CurationPaidRepository;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PointServiceTest extends DummyObject {
    @InjectMocks
    private PointService pointService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CurationPaidRepository curationPaidRepository;

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private CurationBookmarkRepository curationBookmarkRepository;

    @DisplayName("포인트가 부족할 경우 큐레이션 구매에 실패한다.")
    @Test
    public void curation_pay_less_point_fail_test() throws Exception{
        //given
        Member member = newMember("1111", "1111").setPoint(100);

        //stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        //when

        //then
        assertThrows(CustomApiException.class, () -> pointService.payForCuration(1L, 1L));
    }

    @DisplayName("이미 구매한 큐레이션은 다시 구매할 수 없다.")
    @Test
    public void curation_pay_again_test() throws Exception{
        //given
        Member member = newMember("1111", "1111");
        CurationPaid curationPaid = newCurationPaid(member, List.of(1L));

        //stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(curationPaidRepository.findById(any())).thenReturn(Optional.of(curationPaid));

        //when
        //then
        assertThrows(CustomApiException.class, () -> pointService.payForCuration(1L, 1L));
        assertThrows(UnsupportedOperationException.class, () -> pointService.payForCuration(2L, 1L));   // 위 테스트를 통과하여, 아래 stub 을 정의하지 않아서 발생하는 예외

    }

    @DisplayName("큐레이션을 구매하면 사용자의 포인트가 차감된다.")
    @Test
    public void curation_pay_sub_member_point_test() throws Exception{
        //given
        Member member = newMember("1111", "1111");
        Integer beforeMemberPoint = member.getPoint();
        CurationPaid curationPaid = newCurationPaid(member, new ArrayList<>(Arrays.asList(100L)));

        //stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(curationPaidRepository.findById(any())).thenReturn(Optional.of(curationPaid));
        when(curationRepository.getTitleById(any())).thenReturn("");
        when(curationRepository.getReferenceById(any())).thenReturn(new Curation());


        //when
        pointService.payForCuration(1L, 1L);

        //then
        assertThat(member.getPoint()).isEqualTo(0);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint - PointPaidType.CURATION.getPoint());

    }


}