package com.moment.member.dto;

import com.moment.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JoinMemberDTO {
    //이메일(아이디), 이메일 중복 검사, 이메일인증, 비밀번호, 비밀번호 확인, 이름, 성별, 생년월일
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotNull
    private Gender gender;
    @NotNull
    private LocalDate birth;
}
