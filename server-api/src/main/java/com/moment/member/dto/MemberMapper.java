package com.moment.member.dto;

import com.moment.entity.Member;

public class MemberMapper {
    public static ReqMemberInfo toDto(Member member){
        return ReqMemberInfo.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .gender(member.getGender())
                .profileImageUrl(member.getProfileImageUrl())
                .birth(member.getBirth())
                .userCode(member.getUserCode())
                .connectionUserCode(member.getConnectionUserCode())
                .build();
    }
}
