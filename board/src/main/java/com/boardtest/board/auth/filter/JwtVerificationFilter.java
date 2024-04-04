package com.boardtest.board.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// reqeust 당 한번 실행되는 필터이므로 OncePerRequestFilter 상속
public class JwtVerificationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
    
    // 특정 조건 부합시 Filter 동작 방지
    // 로그인/로그아웃 요청, 비회원 상태에서의 조회
    // 인가가 필요하지 않은 request 에 관해 true 반환 => Filter 의 수행 방지
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        // 
        return true;
    }
}
