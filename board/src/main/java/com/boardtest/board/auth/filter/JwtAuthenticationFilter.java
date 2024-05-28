package com.boardtest.board.auth.filter;

import com.boardtest.board.dto.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

// 인증 필터
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    // @SneakyThrows 를 쓰면 try/catch 문이 생략 가능하지만, 왜 필요한지 이해가 힘들고 좋은 방법인지도 모르겠다.
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        // ID, PW 를 request 를 통해서 받음
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = null;
        try{
            loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
        }catch(IOException ioException){
            log.info(ioException.getMessage());
        }
        log.info("AuthenticationFilter 통과");
        // 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        // 매니저에게 인증처리 위임 -> 적절한 provider 탐색 -> userDetails 에서 사용자 조회 후 패스워드 비교 -> 값이 같다면
        // -> 아래 successfulAuthentication 실행
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain filterChain,
                                         Authentication authentication) throws ServletException, IOException{
        // 인증 성공시 핸들러 호출 후 추가 과정 수행
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authentication);
    }
}
