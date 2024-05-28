package com.boardtest.board.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// JpaRepository 의 구현체는 SimpleJpaRepository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Question 과 Join 을 했지만 Member 의 Column 만 Select
    // @Query("select m from Member m join m.questionList order by m.memberId")

    // Question 과 Fetch Join 을 통해 Member 및 Question Entity 를 전부 조회
    @Query("select m from Member m join fetch m.questionList order by m.memberId")
    List<Member> findAllByOrderByMemberId();

    // Member 가 아닌 Dto 로 변환해서 리스트 반환
    // Class Dto 가 아닌 Interface Dto 사용 이유
    // Projection : DB 에서 Select 할 때 필요한 Column 만을 조회하는 것
    // => 인터페이스 Dto 를 사용해 Entity 전체가 아닌 일부 Column 만을 조회하도록 한다.
    // ※ 반환 타입이 인터페이스이므로 Member Class 가 아닌 Proxy 객체가 반환
    @Query("select m from Member m join fetch m.questionList order by m.memberId")
    List<MemberDto.MemberResponseDtoVerInterface> findAllByOrderByMemberIdVerInterface();

    @Query("select m from Member m join fetch m.questionList order by m.memberId")
    List<MemberDto.MemberResponseDto> findAllByOrderByMemberIdVerClass();

    Optional<Member> findByEmail(String email);
}
