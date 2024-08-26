package com.moment.member.service;

import com.moment.common.dto.ResultDTO;
import com.moment.member.dto.JoinMemberDTO;
import com.moment.member.dto.LoginDTO;
import com.moment.member.dto.ReqEmailDTO;
import com.moment.member.dto.ReqMemberInfo;

public interface MemberService {
    void sendAuthenticationEmail(ReqEmailDTO reqEmailDTO);

    ResultDTO<Object> verifyEmail(String email, String code);

    void join(JoinMemberDTO joinMemberDTO);

    LoginDTO.ResLoginDTO login(LoginDTO.ReqLoginDTO loginDTO);

    void resetPassword(String email);

    ReqMemberInfo getMemberInfo(Long memberId);

    void modifyPassword(Long memberId, String password);

    void validatePassword(Long memberId, String password);
}
