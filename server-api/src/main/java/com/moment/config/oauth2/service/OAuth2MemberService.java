package com.moment.config.oauth2.service;

import com.moment.config.oauth2.dto.GoogleMemberInfo;
import com.moment.config.oauth2.dto.KakaoMemberInfo;
import com.moment.config.oauth2.dto.OAuth2MemberInfo;
import com.moment.config.oauth2.dto.PrincipalDetails;
import com.moment.entity.Member;
import com.moment.enums.Role;
import com.moment.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2MemberInfo memberInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            memberInfo = new GoogleMemberInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
        }
        String provider = memberInfo.getProvider();
        String providerId = memberInfo.getProviderId();
        String email = memberInfo.getEmail();
        String username = memberInfo.getName();
        Role role = Role.ROLE_USER;

        Optional<Member> findMember = memberRepository.findByEmail(memberInfo.getEmail());
        Member member;
        //회원가입
        if(!findMember.isPresent()){
            member = Member.builder()
                    .name(username)
                    .password(bCryptPasswordEncoder.encode(UUID.randomUUID().toString().substring(0, 6)))
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .role(role)
                    .build();
            memberRepository.save(member);
        }else{
            member = findMember.get();
        }
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
