package site.termterm.api.global.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import static site.termterm.api.domain.category.CategoryEnum.*;

import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;

import java.util.List;

@Configuration
public class DummyDevInit extends DummyObject {
    @Profile("dev")
    @Bean
    CommandLineRunner init(MemberRepository memberRepository, TermRepository termRepository, FolderRepository folderRepository, TermBookmarkRepository termBookmarkRepository){
        return args -> {
            Member member1 = memberRepository.save(newMember("This-is-social-id", "this-is@an.email"));
            Member member2 = memberRepository.save(newMember("This-is-social-id", "this-is@an.email"));

            Term term1 = termRepository.save(newTerm("Seamless :: 심리스", "IT 및 디자인 분야에서는 사용자 경험을 향상시키기 위해 요소들이 자연스럽게 조화롭게 어우러지는 상태를 묘사할 때 사용하고, 비즈니스에서는 프로세스나 서비스가 매끄럽고 효율적으로 진행되는 상태를 말해요.", List.of(IT, DEVELOPMENT)));
            Term term2 = termRepository.save(newTerm("PoC :: Proof of Concept", "@@Proof of Concept@@의 약자로 새로운 아이디어, 제품, 서비스 등이 실제로 구현 가능하며 성공할 수 있는지를 입증하는 것을 의미해요. IT에서는 신기술이나 도입 예정 제품을 요구사항에 맞는지 검증하는 절차를 말해요.", List.of(IT, DEVELOPMENT, MARKETING)));
            Term term3 = termRepository.save(newTerm("릴리즈 :: release", "새로운 제품, 소프트웨어, 서비스, 기술 등을 공개하거나 출시하는 것을 의미해요. @@릴리즈@@는 제품이나 서비스를 대중에게 공개하고 사용 가능하게 만드는 과정을 포함해요.", List.of(IT, DEVELOPMENT)));
            Term term4 = termRepository.save(newTerm("ChatGPT :: 챗지피티", "@@GPT@@는 @@Generative Pre-trained Transformer@@의 약어이며, OpenAI가 개발한 프로토타입 대화형 인공지능 챗봇이에요. @@ChatGPT@@는 @@GPT-@@3.5를 기반으로 만들어졌으며, 지도학습과 강화학습을 모두 사용해 파인 튜닝되었어요. 질문을 입력하면 인공지능이 빅데이터 분석을 바탕으로 대화하듯 답을 해주는 방식예요.", List.of(BUSINESS, MARKETING)));
            Term term5 = termRepository.save(newTerm("작업 증명 :: PoW / Proof of Work", "WEB 3.0 용어예요. 컴퓨터 연산 작업을 수행하여 블록체인에 기여하는 대가로 보상을 받는 방식을 말해요. 연산을 위해서는 성능이 우수한 장비를 필요로 하며, P2P 네트워크에서 시간과 비용을 들여 수행된 컴퓨터 연산 작업을 신뢰하기 위해 참여 당사자 간에 간단히 검증하는 방식예요.", List.of(BUSINESS, IT)));
            Term term6 = termRepository.save(newTerm("Terraform :: 테라폼", "@@테라폼@@은 인프라스트럭처를 코드로 관리하는 오픈 소스 도구예요. 클라우드 인프라스트럭처를 생성, 변경 및 버전 관리하는 데에 사용해요.", List.of(IT, DEVELOPMENT)));
            Term term7 = termRepository.save(newTerm("HEX :: 헥스", "@@헥스@@색상은 색상을 16진수 값으로 나타낸 용어예요. 16진수는 주로 프로그래밍 언어에서 색상 코드를 표현하는데 사용되며, CSS 코드에서 색상을 지정할 때 \"#RRGGBB\" 형식으로 사용해요. 색상의 강도를 나타내기 위해 00부터 FF까지의 범위로 표시할 수 있어요. 예를들어 #FF0000은 빨간색 성분이 최댓값 FF이고 녹색, 파란색은 최솟값 00인 가장 순수한 빨간색이에요.", List.of(PM, DESIGN)));
            Term term8 = termRepository.save(newTerm("Chatbot :: 챗봇", "@@챗봇@@ 인공지능 등을 활용하여 사용자와 대화를 나누는 컴퓨터 프로그램이에요. 실제 인간이 직접 응답하는 것처럼 자연스럽고 대화형으로 사용자와 상호작용할 수 있어요.", List.of(PM, DESIGN, IT)));
            Term term9 = termRepository.save(newTerm("B.C.C :: B.C.C", "이메일 수신인이 알 수 없도록, 비공개 발송을 해야하는 사람을 의미해요. @@Blind carbon copy@@의 약자예요.", List.of(PM, BUSINESS, IT)));
            Term term10 = termRepository.save(newTerm("RE :: Reply", "받은 메일에 답장한다는 뜻으로 @@Reply@@의 줄임말이에요.", List.of(PM, MARKETING, IT)));
            Term term11 = termRepository.save(newTerm("OT :: Over Time, Orientation", "오리엔테이션을 말할 때도 @@OT@@가 사용되지만, 보통 초과 근무를 이야기할 때 사용해요. @@Over Time@@의 약자예요.", List.of(PM, MARKETING, IT)));
            Term term12 = termRepository.save(newTerm("송부 :: 송부", "편지나 물품 따위를 부치어 보내는 행위를 말해요. 청자를 높일 때 올바르게 표현하기 위해서는 '@@송부@@드립니다'가 아닌 '@@송부@@해 드립니다'라고 표현해야 해요.", List.of(BUSINESS, MARKETING)));

            List<Term> terms = List.of(term1, term1, term2, term3, term4, term5, term6, term7, term8, term9, term10, term11, term12);
            Folder folder1 = newFolder("새 폴더1", "새 폴더 설명1", member1);
            for (int i = 1; i < 12; i++){
                folder1.getTermIds().add((long) i);
                termBookmarkRepository.save(newTermBookmark(terms.get(i), member1, 1));
            }
            folderRepository.save(folder1);


            Folder folder2 = folderRepository.save(newFolder("새 폴더2", "새 폴더 설명2", member1));
            Folder folder3 = folderRepository.save(newFolder("새 폴더3", "새 폴더 설명3", member2));

        };

    }
}
