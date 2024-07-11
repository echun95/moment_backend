package com.moment.config.oauth2.handler;

import com.moment.config.jwt.provider.JwtProvider;
import com.moment.config.oauth2.dto.PrincipalDetails;
import com.moment.entity.Member;
import com.moment.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

/**
 * OAuth2 인증 성공시 접근하는 핸들러
 * 액세스, 리프레쉬 토큰을 재발급 함.
 * */
@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;
	@Value("${oauth2.redirectUrl}")
	private String redirectUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

		// 사용자 액세스 및 리프레쉬 토큰 발급
		String token = generateTokenByUserId(response, principal);

		response.addHeader("Authorization", "Bearer " + token);

		Cookie jwtCookie = new Cookie("jwt", token);
		jwtCookie.setHttpOnly(false);
		jwtCookie.setSecure(false);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(60 * 60 * 24); // 쿠키 만료 시간 설정 (예: 1일)
		response.addCookie(jwtCookie);
//		response.getWriter().write("{\"token\": \"" + token + "\"}");
//		response.getWriter().flush();

		// 리디렉션 설정
		String targetUrl = determineTargetUrl(request, response, authentication);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// 리디렉션할 URL 설정
		return redirectUrl;
	}

	private String generateTokenByUserId(HttpServletResponse response, PrincipalDetails principalDetails) {
		Long userId = principalDetails.getMember().getMemberId();
		// 회원 DB에 사용자 저장되어 있으면, 액세스 토큰 쿠키 발급 및 리프레쉬 토큰 업데이트
		Optional<Member> member = memberRepository.findById(userId);
		if(member.isPresent()){
			member.get().updateRefreshToken(jwtProvider.generateRefreshTokenFromUserId(userId));
			return jwtProvider.generateAccessTokenFromUserId(userId);
		}
		return null;
	}

	private void generateAuthentication(HttpServletRequest request, PrincipalDetails principalDetails) {
		UsernamePasswordAuthenticationToken auth =
			new UsernamePasswordAuthenticationToken(
					principalDetails,
				null,
					principalDetails.getAuthorities());

		auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
