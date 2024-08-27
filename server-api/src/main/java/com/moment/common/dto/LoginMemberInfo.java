package com.moment.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginMemberInfo {
    private Long memberId;
    private String email;
}
