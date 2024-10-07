package com.moment.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqEmailDTO {
    @Email
    @NotBlank
    private String email;

    public ReqEmailDTO(String email) {
        this.email = email;
    }
}
