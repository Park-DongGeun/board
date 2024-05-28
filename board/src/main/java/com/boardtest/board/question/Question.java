package com.boardtest.board.question;

import com.boardtest.board.audit.Auditable;
import com.boardtest.board.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Question extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    public void removeMember(Member member){
        if(this.member != null){
            // 아래 코드가 실행되기 전까지 member 객체는 프록시 객체
            this.member.getQuestionList().remove(this);
            // LAZY 로 해뒀기 때문에 member 객체는 Proxy 객체이지만
            // 위 코드를 통해 member 객체를 조회함으로서 Select 쿼리가 발생하면서 실 객체를 가져온다.
            // Member 애서 Question 을 향하는 fetchType 이 LAZY 이기 때문에 SELECT 쿼리가 따로 발생
        }

        this.member = member;
    }

    public void removeMember(){
        if(this.member != null) {
            System.out.println("연관관계 삭제 TEST");
            this.member.getQuestionList().remove(this);
            System.out.println(this.member.getQuestionList().contains(this));
            // 자식 Entity 인 Question 에서 부모 Entity 인 Member 객체를 제거했기 때문에 외래키인 memberId 가 null 로 업데이트 되기 때문에
            // Update 쿼리문 생성
            this.member = null;
        }
    }
}
