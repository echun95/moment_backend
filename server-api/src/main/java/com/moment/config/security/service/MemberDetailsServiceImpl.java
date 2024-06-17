package com.moment.config.security.service;

import com.moment.config.oauth2.dto.PrincipalDetails;
import com.moment.entity.Member;
import com.moment.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userEmail) {
		// DB에서 사용자 정보 조회 후 반환
		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new UsernameNotFoundException("User Not Found with userEmail: " + userEmail));

		return new PrincipalDetails(member);
	}
}
