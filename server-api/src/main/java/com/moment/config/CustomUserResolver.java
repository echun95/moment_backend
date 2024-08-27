package com.moment.config;

import com.moment.common.dto.LoginMemberInfo;
import com.moment.config.oauth2.dto.PrincipalDetails;
import com.moment.entity.Member;
import com.moment.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class CustomUserResolver implements HandlerMethodArgumentResolver {
	private final MemberRepository memberRepository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// userDto 가 파라미터에 포함되어 있는지 체크하여 true를 리턴한다.
		return parameter.getParameterType() == LoginMemberInfo.class;
	}

//		Jwt principal = (Jwt)authentication.getPrincipal();
//		return memberRepository.findByEmail()
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
		Member member = principal.getMember();
		return LoginMemberInfo.builder()
				.memberId(member.getMemberId())
				.email(member.getEmail())
				.build();
	}
}