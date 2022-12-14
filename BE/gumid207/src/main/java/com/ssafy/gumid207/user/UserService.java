package com.ssafy.gumid207.user;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.gumid207.dto.TokenDto;
import com.ssafy.gumid207.dto.TokenRequestDto;
import com.ssafy.gumid207.dto.UserLoginDto;
import com.ssafy.gumid207.dto.UserRegisterDto;
import com.ssafy.gumid207.entity.RefreshToken;
import com.ssafy.gumid207.entity.User;
import com.ssafy.gumid207.jwt.JwtUtil;
import com.ssafy.gumid207.jwt.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	@Autowired
	private final UserRepository userRepository;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	
	@Transactional
	//회원가입
	public boolean saveUser(UserRegisterDto params) {
		try {
			
			User user = params.toUser(passwordEncoder);
			userRepository.save(user);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	@Transactional
	//이메일 중복 검사
	public Boolean checkEmail(String email) {
		Optional<User> result = userRepository.findByEmail(email);
		if(result.isPresent()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Transactional
	//이메일 중복 검사
	public Boolean checkNickName(String nickName) {
		Optional<User> result = userRepository.findByNickName(nickName);
		if(result.isPresent()) {
			return false;
		}
		else {
			return true;
		}      
	

	}
	
	@Transactional
	//비밀번호 변경
	public Boolean modifyPass(String email, String newPass) {
		Optional<User> result = userRepository.findByEmail(email);
		System.out.println("new: " + newPass);
		if(result.isPresent()) {
			return true;
		}
		else {
			result.get().modifyPass(newPass);
			return false;
		}
	}
	
	public TokenDto loginUser(UserLoginDto params) {
		
		// 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = params.toAuthentication();
        System.out.println("1");
        System.out.println("authenticationToken: " + authenticationToken);
        
     // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        System.out.println("authentication: " + authentication);
        System.out.println("2");
        
     // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = jwtUtil.generateTokenDto(authentication);
        System.out.println("3");
		
        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return tokenDto;
		
	}
	
	@Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!jwtUtil.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtUtil.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = jwtUtil.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }
}
