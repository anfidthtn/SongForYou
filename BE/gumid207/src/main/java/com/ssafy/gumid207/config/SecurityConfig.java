package com.ssafy.gumid207.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.gumid207.jwt.CustomUserDetailsService;
import com.ssafy.gumid207.jwt.JwtAccessDeniedHandler;
import com.ssafy.gumid207.jwt.JwtAuthenticationEntryPoint;
import com.ssafy.gumid207.jwt.JwtUtil;
import com.ssafy.gumid207.oauth.CustomOAuth2UserService;
import com.ssafy.gumid207.oauth.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity(debug = false)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
///oauth2/authorization/google
//	private final UserRepository userRepo;
//	private final JwtUtilsService jwtUtilService;
	private final ObjectMapper mapper;
	// private final CustomOAuth2UserService oAuth2UserService;
//	private final OAuth2SuccessHandler successHandler;

//	private final GoogleTokenValidate googleTokenValidate;
//	private final NaverTokenValidate naverTokenValidate;
//	private final KaKaoTokenValiate kakaoTokenValidate;

	// ???????????? ???????????? ??????
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
//			throws Exception {
//
//		return authenticationConfiguration.getAuthenticationManager();
//	}
//
	@Bean
	public WebSecurityCustomizer WebSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/test", "/test/**", "/images/**", "/swagger-ui/**",
				"/swagger-resources/**", "/v2/api-docs");

	}

	

//
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
//			throws Exception {
//		http.cors().configurationSource(corsConfigurationSource());
//		// http.cors().disable();// cors ?????? ????????????
//		http.httpBasic().disable(); // ????????? username,password ????????? ?????? ??????
//		http.csrf().disable(); // csrf ?????? ?????? ??????
//		http.anonymous().disable(); // ?????? ????????? ?????? x
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // ????????? ???????????? ???????????? ?????? ?????? ?????? ??????
//
//		// oath ??????????????? ??????,
//		// http.oauth2Login().successHandler(successHandler).userInfoEndpoint()
//		// .userService(oAuth2UserService);
//
////		http.addFilterBefore(new JwtAuthFilter(jwtUtilService, userRepo, mapper),
////				UsernamePasswordAuthenticationFilter.class);
//
////		http.addFilterBefore(new CustomOAuthLoginValidateFilter(googleTokenValidate, naverTokenValidate,
////				kakaoTokenValidate, successHandler), JwtAuthFilter.class);
//
////		http.authorizeHttpRequests((authz) -> {
////			authz.antMatchers("/user/profile").hasRole(Role.TEMP.toString());
////			authz.antMatchers("/customer-center/manager/**").hasRole(Role.MANAGER.toString());
////			authz.antMatchers("/**").hasRole(Role.USER.toString());
////			
////		});
//		http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
//
//			@Override
//			public void handle(HttpServletRequest request, HttpServletResponse response,
//					AccessDeniedException accessDeniedException) throws IOException, ServletException {
//
//				response.getWriter().println(String.format("%s -- %s", "??????", accessDeniedException.getMessage()));
//
//			}
//		}).authenticationEntryPoint(new AuthenticationEntryPoint() {
//
//			@Override
//			public void commence(HttpServletRequest request, HttpServletResponse response,
//					AuthenticationException authException) throws IOException, ServletException {
//
//				System.out.println(authException);
//
//			}
//		});
//
//		// ????????? ????????? ????????? ???????????? ?????? ???????????? ??????
//
//		return http.build();
//	}
//
//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//
//		configuration.addAllowedOriginPattern("*");
//		configuration.addAllowedHeader("*");
//		configuration.addAllowedMethod("*");
//		configuration.setAllowCredentials(true);
//
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return source;
//	}
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
	    DaoAuthenticationProvider bean = new DaoAuthenticationProvider();
	    bean.setHideUserNotFoundExceptions(false);
	    bean.setUserDetailsService(customUserDetailsService);
	    bean.setPasswordEncoder(passwordEncoder());
	    
	    return bean;
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception{
			// CSRF ?????? Disable
			http.csrf().disable()

            // exception handling ??? ??? ????????? ?????? ???????????? ??????
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)


            // ??????????????? ??????????????? ????????? ??????
            // ???????????? ????????? ???????????? ?????? ????????? ?????? ????????? Stateless ??? ??????
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            // ?????????, ???????????? API ??? ????????? ?????? ???????????? ????????? ???????????? ????????? permitAll ??????
            .and()
            .authorizeRequests()
            .antMatchers("/user/**").permitAll()
            .anyRequest().authenticated()   // ????????? API ??? ?????? ?????? ??????
            
         // JwtFilter ??? addFilterBefore ??? ???????????? JwtSecurityConfig ???????????? ??????
            .and()
                .apply(new JwtSecurityConfig(jwtUtil))
            
            .and()
            .logout()
                .logoutSuccessUrl("/")
            
            .and()
            .oauth2Login() 
            	.successHandler(successHandler)
                	.userInfoEndpoint()
                    	.userService(customOAuth2UserService);

            
    }
}

