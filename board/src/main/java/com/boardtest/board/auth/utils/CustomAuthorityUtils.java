package com.boardtest.board.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminMailAddress;
    private final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
    private final List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");
    private final List<String> ADMIN_ROLES_STRING = List.of("ADMIN", "USER");
    private final List<String> USER_ROLES_STRING = List.of("USER");


    // 이메일 입력
    // 두 개의 createAuthorities 메서드는 매개변수 타입의 차이로 오버로딩
    // 메모리 상의 ROLE 기반으로 권한 정보 생성
    public List<GrantedAuthority> createAuthorities(String email){
        if(adminMailAddress.equals(email)) return ADMIN_ROLES;
        else return USER_ROLES;
    }

    // 이메일로 생성된 권한 입력
    // DB에 저장된 ROLE 기반으로 *권한 정보* 생성
    public List<GrantedAuthority> createAuthorities(List<String> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    // 회원가입 때 이메일이 관리자라면 관리자 권한 생성
    // 회원이라면 회원 권한 생성
    // DB 에 저장하기 위해 List<String>
    public List<String> createRoles(String email){
        if(adminMailAddress.equals(email)) return ADMIN_ROLES_STRING;
        else return USER_ROLES_STRING;
    }
}
