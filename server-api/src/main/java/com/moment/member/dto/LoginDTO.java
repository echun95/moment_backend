package com.moment.member.dto;

import com.moment.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class LoginDTO {

    @Getter
    @NoArgsConstructor
    public static class ReqLoginDTO{
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String password;

        public ReqLoginDTO(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
    @Getter
    @Builder
    public static class ResLoginDTO{
        private String accessToken;
        private Role userRole;
        private String userName;
        private boolean tempPasswordActive;
    }
}
