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
import site.termterm.api.global.handler.exceptions.CustomStatusApiException;
import site.termterm.api.global.vo.SystemVO;

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

    @DisplayName("폴더 구매에 성공한다.")
    @Test
    public void folder_pay_success_test() throws Exception{
        //given
        Member member = newMember("1111", "1111").setPoint(2000);
        Integer beforeMemberPoint = member.getPoint();
        Integer beforeMemberFolderLimit = member.getFolderLimit();

        //stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        //when
        pointService.payForFolder(1L);

        //then
        assertThat(member.getFolderLimit()).isEqualTo(beforeMemberFolderLimit + 1);
        assertThat(member.getFolderLimit()).isEqualTo(4);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint - PointPaidType.FOLDER.getPoint());
        assertThat(member.getPoint()).isEqualTo(1000);

    }

    @DisplayName("폴더 구매에 실패한다. - 포인트 부족")
    @Test
    public void folder_pay_fail1_test() throws Exception{
        //given
        Member member = newMember("1111", "1111").setPoint(900);

        //stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        //when

        //then
        assertThrows(CustomStatusApiException.class, () -> pointService.payForFolder(1L));

    }

    @DisplayName("폴더 구매에 실패한다. - 생성 한도 초과")
    @Test
    public void folder_pay_fail2_test() throws Exception{
        //given
        Member member = newMember("1111", "1111").setPoint(1200).setFolderLimit(SystemVO.SYSTEM_FOLDER_LIMIT);


        //stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        //when

        //then
        assertThrows(CustomStatusApiException.class, () -> pointService.payForFolder(1L));

    }

    @DisplayName("Member 엔티티에서 폴더 한도를 시스템 한도보다 크게 하려고 시도하면 예외를 던진다.1")
    @Test
    public void throw_exception_when_try_to_add_folder_limit_over_system_limit_test() throws Exception{
        //given
        Member member = newMockMember(1L, "", "").setFolderLimit(9);

        //when
        //then
        assertThrows(RuntimeException.class, () -> member.addFolderLimit());

    }

    @DisplayName("Member 엔티티에서 폴더 한도를 시스템 한도보다 크게 하려고 시도하면 예외를 던진다.2")
    @Test
    public void throw_exception_when_try_to_set_folder_limit_over_system_limit_test() throws Exception{
        //given
        Member member = newMockMember(1L, "", "");

        //when
        //then
        assertThrows(RuntimeException.class, () -> member.setFolderLimit(10));

    }

}