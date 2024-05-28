package com.boardtest.board;

import com.boardtest.board.member.Member;
import com.boardtest.board.member.MemberDto;
import com.boardtest.board.member.MemberRepository;
import com.boardtest.board.member.MemberService;
import com.boardtest.board.question.Question;
import com.boardtest.board.question.QuestionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class JpqlAndDslTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;
    @Test
    @Transactional(readOnly = true)
    void 회원전체조회_JPQL_Tests(){
        List<Member> allMembers = memberRepository.findAllByOrderByMemberId();


        for(Member member : allMembers){
            System.out.println("MemberId : " + member.getMemberId() + ", " + "QuestionId" + member.getQuestionList() + " => " + member.getUserName());

            // 일반 Join 을 사용할 경우 question Entity 에 접근 할 때마다 추가적인 select 쿼리 발생
            // => 일반 Join 은 Member 와 Question 을 Join 하긴 하지만 질의하는 대상 Entity 만 영속화

            // Fetch Join 을 사용할 경우 첫 Select 쿼리 한 개 발생
            // => Fetch Join 은 Fetch Join 이 걸린 Entity 또한 영속화
            member.getQuestionList().stream()
                    .forEach(question -> System.out.println(question.getQuestionId() + ": " + question.getTitle()));
        }

        // Join 하지 않은 다른 Member Entity 조회를 통해 추가 Select 쿼리 발생
        memberRepository.findById(16L).get();

        // JPQL 을 사용했으므로 DB 로 다이렉트로 쿼리가 발생함.
        // 영속화 되어있음을 확인하기 위해 Join 한 Member Entity 를 재조회 했지만 추가 Select 쿼리가 발생하지 않았으므로 영속화 확인
        // Question Entity 또한 같음.
        Assertions.assertEquals(memberRepository.findById(34L).get(), allMembers.get(0));
        Assertions.assertEquals(questionRepository.findById(24L).get(), allMembers.get(1).getQuestionList().get(0));
    }

    @Test
    @Transactional
    void MemberDto_인터페이스_Test(){
        List<MemberDto.MemberResponseDtoVerInterface> allMembers = memberRepository.findAllByOrderByMemberIdVerInterface();

        for(MemberDto.MemberResponseDtoVerInterface member : allMembers){
            // 위 테스트에서 member.getClass() 를 출력해보면 실제 Member 클래스가 출력돼지만
            // 아래의 경우는 Proxy 객체가 출력된다.
            // => 반환 타입을 Interface 로 했기 때문에 프록시를 통해 가짜 구현 객체를 만들고 getter() 를 통해 데이터를 가져올 수 있다.
            System.out.println(member.getClass());
            System.out.println("MemberId : " + member.getMemberId() + ", " + "QuestionId" + member.getQuestionList() + " => " + member.getUserName());
        }
    }

}
