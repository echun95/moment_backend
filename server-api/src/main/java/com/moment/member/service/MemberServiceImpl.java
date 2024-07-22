package com.moment.member.service;

import com.moment.common.dto.ResultDTO;
import com.moment.common.exception.RestApiException;
import com.moment.common.exception.member.MemberErrorCode;
import com.moment.config.jwt.provider.JwtProvider;
import com.moment.entity.Member;
import com.moment.enums.Role;
import com.moment.mail.service.EmailService;
import com.moment.member.dto.JoinMemberDTO;
import com.moment.member.dto.LoginDTO;
import com.moment.member.dto.ReqEmailDTO;
import com.moment.member.repository.MemberRepository;
import com.moment.redis.service.RedisService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final EmailService emailService;
    @Value("${spring.mail.username}")
    private String momentAdminEmail;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void sendAuthenticationEmail(ReqEmailDTO reqEmailDTO) {
        String targetEmail = reqEmailDTO.getEmail();
        int authenticationNum = createRandomNumber();
        //email 발송
        try {
            emailService.sendEmailCertification(targetEmail, momentAdminEmail, authenticationNum);
        } catch (MessagingException e) {
            log.error("failed sendAuthenticationEmail : ", e);
            throw new RestApiException(MemberErrorCode.FAILED_SEND_AUTHENTICATION_EMAIL);
        }
        //redis 저장
        redisService.setValues(targetEmail, String.valueOf(authenticationNum), Duration.ofSeconds(30));
    }

    @Override
    public ResultDTO<Object> verifyEmail(String email, String code) {
        String authenticationNum = redisService.getValue(email);
        //인증 완료
        if (!authenticationNum.equals(code)) {
            throw new RestApiException(MemberErrorCode.FAILED_VERIFY_EMAIL);
        }
        return ResultDTO.of(10000, "이메일 인증을 완료했습니다.", null);
    }

    @Override
    @Transactional
    public void join(JoinMemberDTO joinMemberDTO) {
        validateDuplicateMember(joinMemberDTO.getEmail());
        Member joinMember = createMember(joinMemberDTO);
        memberRepository.save(joinMember);
    }

    @Override
    @Transactional
    public LoginDTO.ResLoginDTO login(LoginDTO.ReqLoginDTO loginDTO) {
        Member findMember = getMemberByEmail(loginDTO.getEmail());
        validatePassword(loginDTO.getPassword(), findMember.getPassword());
        //jwt 발급 및 refresh token 저장
        String accessToken = jwtProvider.generateAccessTokenFromUserId(findMember.getMemberId());
        String refreshToken = jwtProvider.generateRefreshTokenFromUserId(findMember.getMemberId());
        findMember.updateRefreshToken(refreshToken);
        LoginDTO.ResLoginDTO resLoginDTO = LoginDTO.ResLoginDTO.builder()
                .userRole(findMember.getRole())
                .accessToken(accessToken)
                .userName(findMember.getName())
                .build();
        return resLoginDTO;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.FAILED_LOGIN));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RestApiException(MemberErrorCode.FAILED_LOGIN);
        }
    }

    private void validateDuplicateMember(String email) {
        memberRepository.findByEmail(email).ifPresent(member -> {
            throw new RestApiException(MemberErrorCode.DUPLICATE_MEMBER);
        });
    }

    private Member createMember(JoinMemberDTO joinMemberDTO) {
        return Member.builder()
                .email(joinMemberDTO.getEmail())
                .name(joinMemberDTO.getName())
                .gender(joinMemberDTO.getGender())
                .birth(joinMemberDTO.getBirth())
                .role(Role.ROLE_USER)
                .password(bCryptPasswordEncoder.encode(joinMemberDTO.getPassword()))
                .build();
    }

    // 랜덤으로 숫자 생성
    public static int createRandomNumber() {
        return (int) (Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }
}
