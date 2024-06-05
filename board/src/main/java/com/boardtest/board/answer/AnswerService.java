package com.boardtest.board.answer;

import com.boardtest.board.auth.utils.CustomAuthorityUtils;
import com.boardtest.board.exception.BusinessException;
import com.boardtest.board.exception.ExceptionCode;
import com.boardtest.board.member.Member;
import com.boardtest.board.member.MemberRepository;
import com.boardtest.board.question.Question;
import com.boardtest.board.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CustomAuthorityUtils authorityUtils;

    @Transactional
    public void replyAnswer(AnswerDto.AnswerPostDto dto){
        // 회원이 존재하면서 관리자 계정 확인
        Member member = verifiedMemberAndAdminById(dto.getMemberId());
        // 질문 게시글이 존재하는지
        Question question = verifiedQuestionById(dto.getQuestionId());
        // 답변 생성
        Answer answer = Answer.builder()
                .content(dto.getContent())
                .build();

        // 연관관게 설정(One To One)
        member.addAnswer(answer);
        question.replyAnswer(answer);

        // 질문글의 공개 여부에 따른 답변 공개 여부 확인
        answer.setPrivate(question.getIsPrivate());

        answerRepository.save(answer);
    }

    @Transactional
    public void removeAnswer(AnswerDto.AnswerDeleteDto dto){
        // 괸리자 계정
        Member member = verifiedMemberAndAdminById(dto.getMemberId());
        // 해당 답글이 존재하는지
        Answer answer = verifiedAnswerById(dto.getAnswerId());

        // 연관관계 끊기
        member.removeAnswer(answer);
        answer.getQuestion().removeAnswer(answer);
        // DB 에서 완전 삭제
        answerRepository.delete(answer);
    }

    @Transactional
    public Answer modify(AnswerDto.AnswerModifyDto dto){
        Member member = verifiedMemberAndAdminById(dto.getMemberId());

        Answer answer = verifiedAnswerById(dto.getAnswerId());

        answer.setContent(dto.getContent());

        return answer;
    }

    // 일반 회원이 아닌 ADMIN 계정 확인
    private Member verifiedMemberAndAdminById(Long memberId){
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
        
        // 이메일 판단 or 권한
        if(findMember.getEmail().equals(authorityUtils.getAdminMailAddress())) {
            throw new BusinessException(ExceptionCode.MEMBER_IS_NOT_ADMIN);
        }

        return findMember;
    }

    private Question verifiedQuestionById(Long questionId){
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.QUESTION_NOT_FOUND));
    }

    private Answer verifiedAnswerById(Long answerId){
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ANSWER_NOT_FOUND));
    }
}
