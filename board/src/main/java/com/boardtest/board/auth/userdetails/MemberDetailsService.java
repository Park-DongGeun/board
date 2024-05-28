package com.boardtest.board.auth.userdetails;

import com.boardtest.board.auth.utils.CustomAuthorityUtils;
import com.boardtest.board.member.Member;
import com.boardtest.board.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    // 의문
    // => MemberDetailsService 는 DaoAuthenticationProvider 에서 사용된다.
    // 보면 setUserDetailsService 가 존재하는데 이 클래스에서 어떤 필드가 사용됨을 모를텐데
    // 가능할까? 싶었다.
    // @RequiredArgsConstructor 는 생성자 주입을 해주는 역할인데, repository 와 authoorityUtils 를 new UserDetailsService(repository, utils)
    // 없이 주입했냐였다.

    // 해결
    // 스프링은 빈으로 등록된 객체들만 의존성을 자동으로 주입받는다고 한다.
    // MemberDetailsService 는 @Component 를 통해 빈으로 등록하였고, 컴포넌트 스캔 때 두 개의 필드 객체를 주입받는 것

    // 두 개의 필드가 빈으로 등록되었다는 것은 알고 있었고, 스프링이 컨테이너를 통해 주입해주는 것(DI) 또한 알고 있었다.
    // 그러면 왜 의문이 들었나 싶겠지만, UserAuthenticationSuccessHandler 의 경우
    // 필드가 전부 빈으로 등록되어있지만 config 에서 new UserAuthenticationSuccessHandler(객체 전달) 를 통해 내가 직접 주입을 해주었기 때문이다.
    // 그래서, 둘의 차이가 궁금했고 결과적으로 핸들러는 빈으로 등록되어 있지 않고 내가 필요한 타이밍에 직접 객체를 생성해 주입해주기 때문
    // 따라서, 스프링은 핸들러가 빈이 아니기 때문에 필드들을 자동 주입해주지 않고 개발자인 내가 직접 필드들을 생성자를 통해 주입해주는 것이다.

    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils authorityUtils;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일을 통해 회원 존재 여부 확인
        // 없다면 존재하지 않는 회원 에러 발생
        Member findMember = memberRepository.findByEmail(email).get();
        // 있다면 UserDetails 를 구현한 MemberDetails 반환
        return new MemberDetails(findMember);
    }



    private final class MemberDetails extends Member implements UserDetails{
        public MemberDetails(Member member){
            setMemberId(member.getMemberId());
            setEmail(member.getEmail());
            setUserName(member.getUserName());
            setRoles(member.getRoles());
            setPassword(member.getPassword());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(this.getRoles());
        }

        // getPassword() 의 경우 Member 를 상속받았기 때문에 super.getPassword() 가 존재하므로 오버라이딩이 필요없다.
        // getUsername() 도 super.getUsername() 이 존재하지만 이와 같이 할 경우 원하는 email 이 아닌 실제 userName 이 반환되기에
        // 반환값을 this.getEmail() 로 오버라이딩 할 필요가 있었다.
        @Override
        public String getUsername() { return this.getEmail();}

        @Override
        public boolean isAccountNonExpired() { return true; }

        @Override
        public boolean isAccountNonLocked() { return true; }

        @Override
        public boolean isCredentialsNonExpired() { return true; }

        @Override
        public boolean isEnabled() { return true; }
    }
}
