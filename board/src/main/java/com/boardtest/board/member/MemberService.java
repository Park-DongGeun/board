package com.boardtest.board.member;

import com.boardtest.board.auth.utils.CustomAuthorityUtils;
import com.boardtest.board.exception.BusinessException;
import com.boardtest.board.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;
    @Transactional
    public void signUpMember(MemberDto.MemberPostDto dto){
        // 회원 존재 확인
        verifiedMember(dto.getEmail());

        List<String> roles = authorityUtils.createRoles(dto.getEmail());
        // 회원 생성
        Member newMember = Member.builder()
                .email(dto.getEmail())
                .userName(dto.getUserName())
                .roles(roles)
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        // 회원 저장
        memberRepository.save(newMember);
    }
    @Transactional
    public void updateMember(){

    }

    // 회원가입 시 이메일로 회원 여부 확인
    private void verifiedMember(String email){
        Optional<Member> findMember = memberRepository.findByEmail(email);

        if(findMember.isPresent()) throw new BusinessException(ExceptionCode.EXIST_MEMBER);
    }

    // 식별자를 통해 회원 존재 여부 확인
    public Member verifiedMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND)
        );
    }
}
