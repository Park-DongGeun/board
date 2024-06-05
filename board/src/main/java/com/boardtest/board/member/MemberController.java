package com.boardtest.board.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Controller + RequestBody 의 역할
@RequiredArgsConstructor
@RequestMapping("/member") // URI 매칭
public class MemberController {
    private MemberService memberService;
    // 로그아웃
    
    // 회원가입 POST
    @PostMapping
    public ResponseEntity signUp(@RequestBody MemberDto.MemberPostDto dto){
        memberService.signUpMember(dto);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    // 회원탈퇴 
    // 회원 탈퇴는 DB 에서 데이터 삭제가 아닌 상태 정보를 변경

    // 회원조회


    // 회원수정
    // 의문
    // => 회원 정보 수정을 할 때 필요한 정보를 Body 로 넘겨받을 것이다.
    // 수정이 성공하고 DB 에 반영된 것을 확인시키기 위해 반영된 정보를
    // 다시 클라이언트로 보내 반영시켜줘야 하지 않을까 싶다.
    // 기존 프로젝트에서는 200 OK 코드만 보내줬는데, 그 이유가
    // 프론트 쪽에서 먼저 사용자가 입력한 데이터를 갱신하고 서버에 보내주기 때문에
    // 적용이 되었다는 OK 싸인만 보내주면 문제없겠다 생각하지만, 보다 정보의 확실성을 위해서는
    // DB 에 적용된 정보를 그대로 다시 프론트로 보내 업데이트 시키는 것이 옳바르다 생각한다.
}
