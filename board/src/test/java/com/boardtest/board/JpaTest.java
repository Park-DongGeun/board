package com.boardtest.board;

import com.boardtest.board.member.Member;
import com.boardtest.board.member.MemberRepository;
import com.boardtest.board.member.MemberService;
import com.boardtest.board.question.QuestionRepository;
import com.boardtest.board.question.Question;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JpaTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private QuestionRepository questIonRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private EntityManager entityManager;

    @Test
    void persistTest(){
        // 비영속
        Member member = Member.builder()
                .userName("PERSIST")
                .build();

        // 영속성 컨텍스트에 저장
        // 쓰기 지연 저장소에 insert SQL 문 저장
        entityManager.persist(member);

        Member findMember = entityManager.find(Member.class, member.getMemberId());

        Assertions.assertEquals(member, findMember);
        entityManager.flush();
    }
    @Test
    void 저장_테스트(){
        // 비영속
        Member member = Member.builder()
                .userName("MapleStory")
                .build();

        // 영속성 컨텍스트(1차 캐시)에 저장되지만 save 의 트랙잭션으로 인해 DB 커밋
        memberRepository.save(member);

        // DB 에서 영속성 컨텍스트(1차 캐시) 로 entity 를 가져옴
        // => 1차 캐시를 먼저 보고 없으면 DB 에 접근
        // 결과: 두 개의 객체가 같다고 생각했지만 결과는 다름
        // => select sql 이 두번 날라가는 것을 확인
        // 이유: EntityManager 는 트랜잭션(요청) 단위로 생성
        // 두 개의 find 메서드가 같은 단위 내에 요청이 아닌 다른 단위 내 요청이므로
        // findMember1 을 1차 캐시로 가져오지만 바로 죽고
        // findMember2 를 find 할 때 새로 DB에 접근하는 것
//        Member findMember1 = memberRepository.findById(1L).get();
//        Member findMember2 = memberRepository.findById(1L).get();
//
//        Assertions.assertNotEquals(member, findMember1);
//        Assertions.assertEquals(member.getUserName(), findMember1.getUserName());
//        Assertions.assertEquals(findMember1, findMember2);

        // 그럼 이 둘을 같은 트랜잭션으로 묶는다면?
        // 예상: true, true, true
        두개의_엔티티_조회_테스트(member);
    }

    // readOnly = true => 조회용임을 확실히 명시
    @Test
    // Rollback = false 가 없다면 DB 에 엔티티 저장 x
    @Transactional
    void 저장후_두개의_엔티티_조회_테스트(){
            // 비영속
            Member member = Member.builder()
                    .userName("Sibal")
                    .build();

            // 영속화
            // 쓰기 지연 저장소에 insert 문 저장
            // 1차 캐시에 entity 저장
            memberRepository.save(member);

            // 1차 캐시에 존재하는 Entity 를 가져옴
            Member findMember1 = memberRepository.findById(member.getMemberId()).get();
            // 1차 캐시에 존재하는 Entity 를 가져옴
            Member findMember2 = memberRepository.findById(member.getMemberId()).get();

            System.out.println(findMember1 == findMember2);
            Assertions.assertEquals(member, findMember1);
            Assertions.assertEquals(member.getMemberId(), findMember1.getMemberId());
            Assertions.assertEquals(member.getUserName(), findMember1.getUserName());
            Assertions.assertSame(findMember1, findMember2);
    }
    // Q. 두개의_엔티티_조회_테스트
    // 1. 트랜잭션이 없다면 두 번의 select 쿼리문 발생
    // 2. 트랜잭션이 있다면 한 번의 select 쿼리문 발생
    // => 두개의 Member Entity 가 같음을 증명
    // 3. 메서드 호출로 하는 테스트는 두 번의 select 쿼리문 발생
    // =>
    // 하나의 트랜잭션으로 묶었기에 두 개의 엔티티는 같은 영속성 컨텍스트에서 조회가 된다고 생각한다.
    // 하지만, 결과는 다른데 왜 그럴까? 2번과의 차이는 무엇일까?
    @Transactional(readOnly = true)
    void 두개의_엔티티_조회_테스트(Member member){
        // Member member = new Member();
        // member.setMemberId(29L);
        // DB 에서 1차 캐시로 엔티티 영속화
        Member findMember1 = memberRepository.findById(member.getMemberId()).get();
        // 1차 캐시에 존재하는 Entity 를 가져옴
        Member findMember2 = memberRepository.findById(member.getMemberId()).get();

        System.out.println(findMember1 == findMember2);
        Assertions.assertEquals(member.getMemberId(), findMember1.getMemberId());
        Assertions.assertSame(findMember1, findMember2);
    }


    @Test
    @Transactional
    void 생성_조회_테스트(){
        // 비영속
        Member member = Member.builder()
                .userName("Park Dong Keun")
                .build();

        // 영속
        // save()
        // 이 때 DB 에 flush 작용?
        // save 메서드를 살펴보면 메서드 단위에 @Transactional 이 존재한다.
        // 즉, save() 는 하나의 트랜잭션 단위이며 종료와 동시에 DB 에 커밋된다.

        // 하지만, 이 test 의 경우 메서드에 @Transactional 을 추가로 걸었기 때문에
        // save 를 해도 쓰기 지연 저장소에 INSERT 가 저장되고 entity 또한 1채 캐시에 저장되어있다.
        memberRepository.save(member);


        // @Transactional 이 없다면 조회 시 DB 에서 영속성 컨텍스트(1차 캐시)로 엔티티를 가져옴(영속)
        // @Transactional 이 있기 때문에 조회 시 영속성 컨텍스트(1차 캐시)에 존재하는 Entity 를 가져옴
        // => SELECT 쿼리문이 안날라간다.
        Member findMember = memberRepository.findById(member.getMemberId()).get();
        // UserName 은 같다.
        Assertions.assertEquals(member.getUserName(), findMember.getUserName());

        // @Transactional 없다면 Member 와 방금 DB 에서 들고온 findMember 는 다를 수 밖에 없다.
        // @Transactional 를 통해 하나의 작업 단위로 묶는다면 save 를 커밋하지 않고 1차 캐시에 존재하는 Entity 를 가져오므로
        // 같은 Member 객체임을 확인할 수 있다.
        Assertions.assertEquals(member, findMember);
    }
    // 삭제
    @Test
    void 삭제(){
        Member member = new Member();
        memberRepository.deleteById(14L);
    }

    // 업데이트
    @Test
    @Transactional
    // @Rollback(value = false)
    // => @Transactional 은 테스트 케이스에 선언시 테스트 시작전에 트랜잭션을 시작하고,
    // 테스트 완료 후 항상 롤백을 하여, 다음 테스트에 영향을 주지 않는다.

    // 따라서, JPA 에서는 조회만 하고 업데이트 쿼리문을 날리지 않고 롤백하기 때문에
    // @Transactional 를 사용해도 더티 체킹을 확인하지 못했다.
    // 그래서, service 를 통한 업데이트를 하도록 했는데
    // Test 단계에서 Repository Layer 를 테스트 하기위해선 Rollback = false 로 설정하면
    // 롤백하지 않고 업데이트를 반영하기 때문에 더티 체킹이 되는것을 확인할 수 있다.
    // ※ save() 는 메서드에 @Transactional 이 존재하기 때문에 롤백이랑 무관
    void 더티체킹_save_테스트(){
        // findById vs getReferenceById 는 뭘까?
        // 1. Rollback = false
        Member member = memberRepository.findById(19L).orElseThrow();
        member.setUserName("동근 메이플 285");

        // 2. Service 사용
        // Test 내에서 set 하지 않고 service 를 통해 업데이트 했더니 save 없이 업데이트 성공
//        Member member = Member.builder()
//                .memberId(15L)
//                .userName("동근 팍씨")
//                .build();
//        memberService.updateMember(member);

        // 3. save 사용
        // => 하지만 Rollback = false 가 없으면 업데이트가 반영되지 않는다.
        memberRepository.save(member);

        // Question 내에서 Member 를 Lazy(지연로딩) 로 설정했기 때문에 Member 는 조회할 수 없다.
//        Question question = questIonRepository.findById(13L).orElseThrow();
//        System.out.println(question.getTitle());
//        System.out.println(question.getMember().getUserName());
    }

}
