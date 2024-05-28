package com.boardtest.board.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void updateMember(Member member){
        Member findMember = memberRepository.findById(member.getMemberId()).get();
        findMember.setUserName(member.getUserName());
    }
}
