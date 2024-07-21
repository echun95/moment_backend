package com.moment.member.service;

import com.moment.common.dto.ResultDTO;
import com.moment.member.dto.JoinMemberDTO;
import com.moment.member.dto.ReqEmailDTO;

public interface MemberService {
    void sendAuthenticationEmail(ReqEmailDTO reqEmailDTO);

    ResultDTO<Object> verifyEmail(String email, String code);

    void join(JoinMemberDTO joinMemberDTO);
}
