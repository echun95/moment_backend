package com.moment.member.service;

import com.moment.common.exception.RestApiException;
import com.moment.common.exception.member.MemberErrorCode;
import com.moment.common.service.AwsFileService;
import com.moment.config.jwt.provider.JwtProvider;
import com.moment.entity.Member;
import com.moment.enums.Role;
import com.moment.mail.service.EmailService;
import com.moment.member.dto.*;
import com.moment.member.repository.MemberRepository;
import com.moment.redis.service.RedisService;
import com.moment.util.RandomUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final EmailService emailService;

    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final RandomUtils randomUtils;
    private final AwsFileService awsFileService;
    private static final String TEMP_PASSWORD_PREFIX = "temp_password_";

    @Override
    public void sendAuthenticationEmail(ReqEmailDTO reqEmailDTO) {
        Optional<Member> findMember = memberRepository.findByEmail(reqEmailDTO.getEmail());
        if(findMember.isPresent()){
            throw new RestApiException(MemberErrorCode.ALREADY_BEEN_JOIN);
        }
        String targetEmail = reqEmailDTO.getEmail();
        int authenticationNum = randomUtils.createRandomNumber();
        //email 발송
        try {
            emailService.sendEmailCertification(targetEmail, authenticationNum);
        } catch (MessagingException e) {
            log.error("failed sendAuthenticationEmail : ", e);
            throw new RestApiException(MemberErrorCode.FAILED_SEND_AUTHENTICATION_EMAIL);
        }
        //redis 저장
        redisService.setValues(targetEmail, String.valueOf(authenticationNum), Duration.ofSeconds(30));
    }

    @Override
    public void verifyEmail(String email, String code) {
        String authenticationNum = redisService.getValue(email);
        //인증 완료
        if (!authenticationNum.equals(code)) {
            throw new RestApiException(MemberErrorCode.FAILED_VERIFY_EMAIL);
        }
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
        //임시 비번 사용 유무 체크
        //redis에 해당 아이디의 키값으로 임시 비번이 있는지 확인 후 조회
        //조회 한 값과 입력받은 값이 같은지 확인 후 같으면 로그인 완료처리 및 active true 반환
        boolean tempPasswordActive = checkTemporaryPassword(loginDTO);
        if (tempPasswordActive) {
            findMember.changePassword(bCryptPasswordEncoder.encode(loginDTO.getPassword()));
            deleteTemporaryPassword(loginDTO.getEmail());
        }else{
            validatePassword(loginDTO.getPassword(), findMember.getPassword());
        }
        //jwt 발급 및 refresh token 저장
        String accessToken = jwtProvider.generateAccessTokenFromUserId(findMember.getEmail());
        String refreshToken = jwtProvider.generateRefreshTokenFromUserId(findMember.getEmail());
        findMember.updateRefreshToken(refreshToken);

        LoginDTO.ResLoginDTO resLoginDTO = LoginDTO.ResLoginDTO.builder()
                .userRole(findMember.getRole())
                .accessToken(accessToken)
                .userName(findMember.getName())
                .tempPasswordActive(tempPasswordActive)
                .build();
        return resLoginDTO;
    }

    @Override
    public void resetPassword(String email) {
        findMemberByEmail(email);
        String temporaryPassword = randomUtils.generateTemporaryPassword();
        saveTemporaryPassword(email, temporaryPassword);
        try {
            emailService.sendTemporaryPassword(email, temporaryPassword);
        } catch (MessagingException e) {
            log.error("failed sendTemporaryPassword : ", e);
            throw new RestApiException(MemberErrorCode.FAILED_SEND_TEMPORARY_PASSWORD_EMAIL);
        }
    }

    @Override
    public ResMemberInfo getMemberInfo(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberMapper.toDto(member);
    }

    @Override
    @Transactional
    public void modifyPassword(Long memberId, String password) {
        Member member = findMemberById(memberId);
        member.changePassword(bCryptPasswordEncoder.encode(password));
    }

    @Override
    public void validatePassword(Long memberId, String password) {
        Member findMember = findMemberById(memberId);
        validatePassword(password, findMember.getPassword());
    }

    @Override
    @Transactional
    public void saveProfile(Long memberId, MultipartFile file) {
        Member findMember = findMemberById(memberId);
        try {
            String profileImageUrl = findMember.getProfileImageUrl();
            if(profileImageUrl != null && !profileImageUrl.isBlank()){
                String fileName = extractFileName(profileImageUrl);
                awsFileService.removeFile(fileName);
            }
            String saveProfilePath = awsFileService.saveProfileImg(file, memberId);
            findMember.changeProfile(saveProfilePath);
        } catch (IOException e) {
            throw new RestApiException(MemberErrorCode.FAILED_UPLOAD_PROFILE);
        }
    }
    public static String extractFileName(String url) {
        // 마지막 슬래시 이후의 문자열 추출
        return url.substring(url.indexOf(".com/") + 5);
    }
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
    }
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    private boolean checkTemporaryPassword(LoginDTO.ReqLoginDTO loginDTO) {
        String temporaryPassword = getTemporaryPassword(loginDTO.getEmail());
        if (temporaryPassword != null && !temporaryPassword.isBlank()) {
            if (temporaryPassword.equals(loginDTO.getPassword())) {
                return true;
            } else {
                throw new RestApiException(MemberErrorCode.FAILED_AUTHENTICATION_TEMPORARY_PASSWORD);
            }
        }
        return false;
    }
    private void deleteTemporaryPassword(String email){
        redisService.deleteValue(TEMP_PASSWORD_PREFIX + email);
    }

    private void saveTemporaryPassword(String email, String temporaryPassword) {
        redisService.setValues(TEMP_PASSWORD_PREFIX + email, temporaryPassword, 10, TimeUnit.MINUTES);
    }
    private String getTemporaryPassword(String email) {
        return redisService.getValue(TEMP_PASSWORD_PREFIX + email);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.FAILED_LOGIN));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RestApiException(MemberErrorCode.FAILED_VALIDATE_PASSWORD);
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
                .userCode(randomUtils.createUserCode())
                .role(Role.ROLE_USER)
                .password(bCryptPasswordEncoder.encode(joinMemberDTO.getPassword()))
                .build();
    }
}
