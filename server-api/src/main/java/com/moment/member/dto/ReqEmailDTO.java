package com.moment.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqEmailDTO {
    @Email
    @NotBlank
    private String email;
}
