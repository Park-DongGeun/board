package com.boardtest.board.answer;

import com.boardtest.board.audit.Auditable;
import com.boardtest.board.member.Member;
import com.boardtest.board.question.Question;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String content;

    private boolean isPrivate;

    @OneToOne
    @JoinColumn(name = "questionId", unique = true)
    private Question question;

    @OneToOne
    @JoinColumn(name = "memberId", unique = true)
    private Member member;
}
