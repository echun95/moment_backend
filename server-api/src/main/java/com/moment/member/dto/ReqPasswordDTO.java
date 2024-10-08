package com.moment.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqPasswordDTO {
    @NotBlank
    private String password;

    public ReqPasswordDTO(String password) {
        this.password = password;
    }
}
