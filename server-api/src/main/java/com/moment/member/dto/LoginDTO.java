package com.moment.member.dto;

import com.moment.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginDTO {

    @Getter
    public static class ReqLoginDTO{
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String password;
    }
    @Getter
    @Builder
    public static class ResLoginDTO{
        private String accessToken;
        private Role userRole;
        private String userName;
    }
}
