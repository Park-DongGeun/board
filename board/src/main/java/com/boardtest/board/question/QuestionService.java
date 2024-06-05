package com.boardtest.board.question;

import com.boardtest.board.exception.BusinessException;
import com.boardtest.board.exception.ExceptionCode;
import com.boardtest.board.member.Member;
import com.boardtest.board.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public void createQuestion(QuestionDto.QuestionPostDto dto){
        Member member = verifiedMemberById(dto.getMemberId());

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .questionStatus(QuestionStatus.QUESTION_REGISTERED)
                .isPrivate(dto.isPrivate())
                .build();


        member.addQuestion(question);

        questionRepository.save(question);
    }

    @Transactional
    public Question removeQuestion(Long questionId){
        Question question = verifiedQuestionById(questionId);
        question.setQuestionStatus(QuestionStatus.QUESTION_DELETED);

        return question;
    }

    private Member verifiedMemberById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private Question verifiedQuestionById(Long questionId){
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.QUESTION_NOT_FOUND));
    }
}
