package com.boardtest.board.member;

import com.boardtest.board.answer.Answer;
import com.boardtest.board.audit.Auditable;
import com.boardtest.board.question.Question;
import jakarta.persistence.*;
import lombok.*;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
// 객체를 builder 를 통해 생성하게 되면 클래스(전역) 변수를 초기화해도 null 값이 들어가게 된다.
// @Builder.Default 를 사용하면 builder 를 사용해도 null 값이 들어가지 않는 것을 확인할 수 있다.
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Member <=> Question 양방향 매핑에서 @ToString 을 양쪽에 사용하면
// 서로가 서로를 무한 참조하여 스택오버플로 발생
// @ToString
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // (= AUTO_INCREMENT), 기본 키 생성을 데이터베이스에 위임
    private Long memberId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // cascade(종속) = CascadeType.REMOVE
    // => 위 코드가 없이 Member 엔티티를 삭제하려 하면 연관돼 있는 Question Entity 로 인해 문제가 생긴다.
    // 이 때 위 코드를 사용 시 Member 엔티티를 삭제할 때 자식 관계인 Question 엔티티도 같이 삭제.
    // 연관관계를 삭제하면 Member Entity 는 고아객체로 남아 DB 에 남는다.
    // ALL = PERSIST + REMOVE
    
    // cascade(종속) = CascadeType.PERSIST(
    // => 부모 엔티티를 영속화(저장)할 때 자식 엔티티도 같이 영속화( 조회할때는 부모만 영속화 )
    // => 두 엔티티를 left join 한 select 쿼리문이 날라감

    // orphanRemoval = true
    // => REMOVE 와 같이 부모 엔티티가 삭제된다면 자식 엔티티도 삭제되는 부분은 동일
    // 하지만, 부모 엔티티에서 자식 엔티티와의 관계를 삭제할 때 REMOVE 는 자식엔티티가 고아객체로 DB 에 남지만
    // orphanRemoval = true 는 고아 객체를 삭제해준다.
    
    // LAZY : Member 객체를 조회할 때 Question 객체는 가져오지 않고 Member 객체에서 getQuestionList 가 필요할 때 가져옴
    // EAGER : Member 객체를 조회할 때 QuestionList 도 같이 가져옴
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "member", orphanRemoval = true) // default : lazy
    @Builder.Default
    // QuestionList 를 필요로 할 때 Select 쿼리문 발생
    private List<Question> questionList = new ArrayList<>();

    @OneToOne
    private Answer answer;

    // 자동으로 MEMBER_ROLES 테이블 생성
    // Member(1) : Member_Roles(N) 관계
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public void addQuestion(Question q){
        q.setMember(this);
        this.questionList.add(q);
    }

    public void addAnswer(Answer answer){
        answer.setMember(this);
        this.answer = answer;
    }

    public void removeAnswer(Answer answer){
        if(this.answer != null){
            this.answer = null;
        }
    }
}
