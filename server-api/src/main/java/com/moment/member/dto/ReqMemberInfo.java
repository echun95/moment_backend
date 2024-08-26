package com.moment.member.dto;

import com.moment.entity.Member;
import com.moment.enums.Gender;
import com.moment.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReqMemberInfo {
    private Long memberId;
    private String email;
    private String name;
    private String phoneNumber;
    private Gender gender;
    private String profileImageUrl;
    private LocalDate birth;
    private String userCode;
    private String connectionUserCode;

    @Builder
    public ReqMemberInfo(Long memberId, String email, String name, String phoneNumber, Gender gender, String profileImageUrl, LocalDate birth, String userCode, String connectionUserCode) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.birth = birth;
        this.userCode = userCode;
        this.connectionUserCode = connectionUserCode;
    }
}
