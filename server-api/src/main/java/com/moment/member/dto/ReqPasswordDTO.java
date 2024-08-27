package com.moment.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqPasswordDTO {
    @NotBlank
    private String password;
}
