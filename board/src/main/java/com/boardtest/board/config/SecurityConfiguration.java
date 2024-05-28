package com.boardtest.board.config;

import com.boardtest.board.auth.filter.JwtAuthenticationFilter;
import com.boardtest.board.auth.filter.JwtVerificationFilter;
import com.boardtest.board.auth.handler.UserAccessDeniedHandler;
import com.boardtest.board.auth.handler.UserAuthenticationFailureHandler;
import com.boardtest.board.auth.handler.UserAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// 설정 파일 및 Bean 을 등록하기 위한 어노테이션
// 해당 어노테이션 사용을 통해 Bean 이 싱글톤으로 SpringContainer 에 저장되도록 한다.
@Configuration
// debug = true 를 할 경우 console 에 security 로그가 뜬다.
// 스프링 측에서 사용하지 말라고 하기 때문에 false 로 설정
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserAccessDeniedHandler userAccessDeniedHandler;

    // FilterChainProxy 에 대한 커스터마이징
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        // 예외처리 하고 싶은 url 설정
//        return web -> web.ignoring()
//                // Spring Security 에서 해당 경로에 대해 필터 적용 무시
//                .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs")
//                // Spring Security 에서 정적자원에 대한 인증 무시
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }


    // 메서드 이름 filterChain 고정
    // SecurityFilterChain 을 구성하는 전반적인 설정 수행
    // HttpSecurity 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // spring boot 가 3.0으로 넘어오면서 and() 메서드가 deprecated 되버림.
        // => 람다식 이용

        // 스프링은 기본적으로 X-Frame-Options Click jacking 공격을 막기 위해 X-Frame-Options 가 deny 로 설정
        // 동일 도메인에서의 iframe 접근 허용
        http.headers((headerConfig) ->
                headerConfig.frameOptions(frameOptionsConfig ->
                        frameOptionsConfig.sameOrigin()
                )
        );
        // X-Frame-Options 을 비활성화
        http.headers((headerConfig) ->
                headerConfig.frameOptions(frameOptionsConfig ->
                        frameOptionsConfig.disable()
                )
        );


        // csrf(Cross Site Request Forgery) : 사이트 간 위조 요청
        // 인증된 사용자의 세션 정보를 이용해 웹 애플리케이션에 요청하는 공격

        // 브라우저를 사용치 않는 클라이언트만 사용하는 서비스 -> Rest API
        // disable 을 하지 않을 경우 get 요청을 제외하면 에러 발생
        // JWT 를 사용하기 위해 아래 설정들을 disable 처리
        http.csrf(csrf -> csrf.disable());
        http.formLogin(login -> login.disable());
        http.httpBasic(HttpBasicConfigurer::disable);
        // spring security 에서 세션 사용 x
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        http.exceptionHandling(exceptionHandle ->
                exceptionHandle.accessDeniedHandler(userAccessDeniedHandler));
        http.cors(Customizer.withDefaults()); // corsConfigurationSource 이름으로 등록된 Bean 사용
        http.apply(new CustomFilterConfigurer()); // 사용자 정의 필터 적용(커스텀 인증/인가/에러 필터 적용)
        http.authorizeHttpRequests(
          auth -> auth
                  .requestMatchers("/members", "/boards").authenticated()
                  .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs")
                        .permitAll()
        );


        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        // CORS 정책 설정
        CorsConfiguration configuration = new CorsConfiguration();
        // 모든 출처(URL) 에 대한 스크립트 기반 Http 통신 허용
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // Http 메서드에 대한 Http 통신 허용
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 응답 헤더 설정
        configuration.setExposedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 URL 에 대해 앞선 cors 정책 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 인증 및 권한 부여를 위해 사용되는 필터 체인 구성하는데 사용
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity>{
        @Override
        public void configure(HttpSecurity builder) throws Exception{
            // builder.getSharedObject 는 맵 형태로 builder 가 관리하는 객체를 반환
            // AuthenticationManager 는 인터페이스로, 구현체 ProviderManager 반환(주입)
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager);
            jwtAuthenticationFilter.setFilterProcessesUrl("/members/login");
            // successHandler 의 경우 빈으로 등록돼지 않은 클래스다. 따라서, 필드들이 전부 빈으로 등록된 객체다 할지라도
            // 생성자의 매개변수로 객체들을 넘겨줘야 할 필요가 있다.
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());
            // 로그인 성공/실패 시 호출하는 핸들러
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter();

            builder
                    .addFilter(jwtAuthenticationFilter)
                    // AuthenticationFilter 실행 후 VerificationFilter 실행
                    .addFilterAfter(jwtVerificationFilter, jwtAuthenticationFilter.getClass());
        }
    }
}
