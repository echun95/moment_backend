package com.moment.member.service;

import com.moment.member.dto.JoinMemberDTO;
import com.moment.member.dto.LoginDTO;
import com.moment.member.dto.ReqEmailDTO;
import com.moment.member.dto.ResMemberInfo;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void sendAuthenticationEmail(ReqEmailDTO reqEmailDTO);

    void verifyEmail(String email, String code);

    void join(JoinMemberDTO joinMemberDTO);

    LoginDTO.ResLoginDTO login(LoginDTO.ReqLoginDTO loginDTO);

    void resetPassword(String email);

    ResMemberInfo getMemberInfo(Long memberId);

    void modifyPassword(Long memberId, String password);

    void validatePassword(Long memberId, String password);

    void saveProfile(Long memberId, MultipartFile file);
}
