package com.boardtest.board;

import com.boardtest.board.member.Member;
import com.boardtest.board.member.MemberRepository;
import com.boardtest.board.question.Question;
import com.boardtest.board.question.QuestionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
// @DataJpaTest
// => Repository Unit 테스트를 위함
// @Transactional 를 가지고 있어 트랜잭션 종료후 롤백
public class CascadeTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager entityManager;
    @Test
    @Transactional
    @Rollback(value = false)
    void eagerTest(){
        Member member = memberRepository.findById(28L).get();

        Question question = Question
                .builder()
                .title("Eager Test")
                .member(member)
                .build();

        questionRepository.save(question);
        member.add(question);

        // flush 는 쓰기 지연 저장소에 있는 SQL 문을 DB 에 날리는 역할
        // commit 은 트랜잭션의 변화를 DB 에 확정적으로 적용하는 역할을 하지만
        // 테스트 환경에서는 트랜잭션이 커밋이 아닌 롤백 돼므로 DB 에 변경점이 반영되지 않는다.
        // 반영하고 싶다면 롤백 설정을 false 로 할 것
        entityManager.flush();
        entityManager.clear();

        // 영속성 컨텍스트 초기화 후 DB 에서 Question, Member Entity 1차캐시로 영속화
        // LAZY 와 다르게 조회 시 JOIN 추가 발생 및 MEMBER 엔티티도 가져옴
        // => Proxy 가 아닌 실제 객체
        Question findQ = questionRepository.findById(question.getQuestionId()).get();
        System.out.println(findQ.getMember().getClass());
        Assertions.assertEquals(findQ.getMember(),
                memberRepository.findById(findQ.getMember().getMemberId()).get());
        Assertions.assertNotEquals(member, findQ.getMember());
        System.out.println(findQ.getMember().getUserName());
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void lazyTest(){
        System.out.println("Member Entity 조회(DB -> 1차 캐시)");
        Member member = memberRepository.findById(34L).get();

        Question question = Question
                .builder()
                .title("LAZY Test")
                .member(member)
                .build();

        System.out.println("Question Entity 영속화");
        questionRepository.save(question);
        member.add(question);

        
        entityManager.flush();
        entityManager.clear();

        System.out.println("Flush 및 영속성 컨텍스트 초기화");

        System.out.println("Question Entity 조회(DB -> 1차 캐시)");
        Question findQ = questionRepository.findById(question.getQuestionId()).get();
        // 기대한 정답 : Member 객체가 Proxy 객체로 출력 및 getUserName() 출력할 때 select 쿼리문 발생

        // 실제 결과 : 실제 Member 객체 출력 및 select 쿼리문 발생 x

        // 이유
        // => @Transactional 로 인해 save 후 커밋이 돼지 않기 때문에
        // 1차 캐시에 동일 Member Entity 가 남아 문제 발생

        // 해결
        // => flush 를 통해 insert sql 문을 날리고 clear 를 통해 영속성 컨텍스트 초기화
        // findQ 를 다시 DB 에서 1차 캐시로 영속 후 member 엔티티를 출력해보면 
        // class com.boardtest.board.member.Member$HibernateProxy$VKMNs0FK 프록시 객체임을 확인
        // 이후 getUserName() 이 출력될 때 select 쿼리문 발생 확인
        System.out.println(findQ.getMember().getClass());
        System.out.println("-----------------------------------------");
        System.out.println(findQ.getMember().getUserName());
    }

    @Test
    @Transactional
    void cascade_REMOVE_TEST(){
        // DB -> 1차 캐시(영속화)
        System.out.println("Member Entity 조회");
        Member member1 = memberRepository.findById(34L).get();


        // ※ 삭제
        // 1차 캐시에 남아 있을것인가 ?
        // => 영속성 컨텍스트에서 삭제 후 쓰기 지연 저장소에 삭제 SQL 문 저장
        // flush 후 DB 에 적용되어 삭제
        // 하지만, 트랜잭션으로 묶었기 때문에 해당 메서드에서는 영속성 컨텍스트에서만 삭제
        // Member Entity 를 찾으면서 Question Entity 도 같이 find()
        // => Question select 쿼리문 발생, Member 는 1차 캐시에 존재
        // 따라서, 쓰기 지연 저장소에 Delete SQL 문이 저장돼 있고
        // 1차 캐시에는 Member Entity, Question Entity 가 존재하지만
        // delete 메서드에 의해 1차 캐시에서 삭제
        System.out.println("Member Entity 삭제");
        memberRepository.delete(member1);
        // entityManager.flush();
        System.out.println("Member Entity 재 조회");

        // ※ 문제점
        // 1차 캐시에 해당 Member Entity 가 존재하지 않으므로 DB 에 쿼리를 날렸지만
        // 존재하지 않는다고 나옴.
        // 삭제 SQL 이 날라가지 않았기 때문에 DB 에는 남아있어야 할텐데 왜 안나올까?
        
        // ※ 예상
        // delete 쿼리문은 날라가지 않았으므로 DB 에는 Member Entity 가 남아있다.
        // 조회를 통해 1차 캐시로 가져오지만 삭제 될 예정이므로 (일관성 유지) 조회하지 못해서 null 값을 반환
        // Member member2 = memberRepository.findById(34L).get();

        System.out.println("Question Entity 조회");
        Assertions.assertEquals(questionRepository.findById(6L).get(), null);
    }

    // orphanRemoval = true 테스트
    // => cascadeType.REMOVE 와 같은 결과
    @Test
    @Transactional
    void orphanRemoval_Test() {
        // DB -> 1차 캐시(영속화)
        System.out.println("Member Entity 조회");
        Member member1 = memberRepository.findById(34L).get();

        System.out.println("Member Entity 삭제");

        memberRepository.delete(member1);

        System.out.println("Question Entity 조회");
        Assertions.assertEquals(questionRepository.findById(6L).get(), null);
    }
    // 연관관계 삭제 후 REMOVE 테스트
    @Test
    @Transactional
    @Rollback(value = false)
    void 연관관계_삭제_VERSION_cascadeType_REMOVE(){
        System.out.println("Question Entity 조회");
        Question question = questionRepository.findById(16L).get();
        System.out.println("연관관계 변경");
        question.removeMember(null);

        System.out.println("Flush And Clear");

        // Question Entity Dirty Checking 확인
        // => 로그를 보면 Question Entity 에 대해 Update 쿼리문이 발생한것을 알 수 있다.
        // 위에서 연관관계를 삭제하면서 Question 객체의 member 필드를 null 로 했고
        // 그에 따라, 영속성 컨텍스트 1차 캐시에 저장된 스냅샷과 다르기 때문에
        // 자동으로 DB 에 update 쿼리문이 발생해 memberId 가 null 로 바뀐것을 확인
        entityManager.flush();
        entityManager.clear();

        question = questionRepository.findById(17L).get();
        System.out.println(question.getTitle());
    }

    @Test
    @Transactional
    // @Rollback(value = false)s
    void 연관관계_삭제_VERSION_orphanRemovalTest(){
        System.out.println("Question Entity 조회");
        Member member = memberRepository.findById(34L).get();
        System.out.println("연관관계 변경");
        System.out.println("Em 내 Size(삭제 전): " + member.getQuestionList().size());
        Question question = member.getQuestionList().get(0);
        member.getQuestionList().get(0).removeMember();
        Member member2 = memberRepository.findById(34L).get();
        // 영속성 내 QuestionList size = 0
        System.out.println(member.equals(member2));
        System.out.println("Em 내 Size(삭제 후): " + member2.getQuestionList().size());
        System.out.println(entityManager.contains(question));
        System.out.println("Flush And Clear");

        // 왜 CascadeType.PERSIST 가 없으면 delete 문이 날라가지 않을까?
        // => PERSIST 는 부모 Entity 를 영속화할 때 자식 Entity 도 자동으로 영속화 하도록 하는 영속성 전이 기술이다.
        // 조회 및 삭제에는 관련이 없기 때문에 PERSIST 로 인해 삭제되었다는 것은 우연일 뿐 delete 쿼리가 날라간 것에는 관련이 없다.

        // 문제
        // => Delete 쿼리문이 발생하지 않음.
        // Member Entity 에서 Question Entity 에 대한 연관관계를 삭제했음에도 Delete 쿼리문이 발생하지 않고
        // 당연히 삭제도 돼지 않아 DB 에 그대로 존재함을 확인

        // 원인
        // hibernate 자체 버그로 인해 orphanRemoval 단독 사용 시 하위 Entity 삭제 실패
        // JPA 스펙상 orphanRemoval = true 만 사용했을 때 연관관계를 삭제하면 Question Entity 또한 삭제돼는 것이 맞지만
        // 버그로 인해 삭제 돼지 않음.

        // 해결
        // CascadeType.ALL or CascadeType.PERSIST 를 같이 사용하면 Delete 쿼리문이 발생함을 확인
        // => PERSIST 로 인해 영향이 생겨 ALL 도 묶은 것
        // 실무에서는 orphanRemoval = true 만 단독으로 사용치 않고 CascadeType 을 같이 사용한다고 하지만, 경험이 부족해 잘 모름
        entityManager.flush();
        entityManager.clear();

        try {
            System.out.println("기존 Member 의 사이즈: " + member.getQuestionList().size());
            System.out.println("새로 호출한 Member 의 사이즈 : " + memberRepository.findById(34L).get().getQuestionList().size());
            question = questionRepository.findById(19L).get();
            System.out.println(question.getTitle());
        } catch (NoSuchElementException e) {
            System.out.println("Question entity is deleted.");
        }
    }

    // cascadeType.PERSIST 테스트
    @Test
    @Transactional
    // @Rollback(value = false)
    public void 영속성전이_PERSIST_TEST(){
        Member member = Member
                .builder()
                .userName("0520 Jpa Test")
                        .build();
        Member savedMember = memberRepository.save(member);

        // false
        System.out.println(member.getQuestionList() instanceof ArrayList);

        Question question1 = Question
                .builder()
                .title("Persist Test 3")
                .build();
        Question question2 = Question
                .builder()
                .title("Persist Test 4")
                .build();


        // Member 에서만 question 에 대한 연관관게를 설정했고
        // Question 에 대해서는 Member 에 대해서 연관관계 값을 갖지 못했기 때문에
        // DB 를 확인하면 FK 인 memberId 는 null 값 출력
        // savedMember.getQuestionList().add(question1);
        // savedMember.getQuestionList().add(question2);

        // add() 메서드를 통해 Question <-> Member 에 대해 연관관계를 설정하면서
        // memberId 확인 가능
        savedMember.add(question1);
        savedMember.add(question2);

        System.out.println("Question Entity Add 후 Flush");
        // Question Entity 들은 따로 save(persist, 영속화) 해주지 않았지만,
        // Member 에서 연관관계를 Persist 로 설정해두었기에 영속화 되어있는 상태이며
        // flush 를 한다면 insert 쿼리문이 발생하는 것을 확인할 수 있다.
        entityManager.flush();
    }
}
