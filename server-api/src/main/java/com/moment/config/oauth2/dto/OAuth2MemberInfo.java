package com.moment.config.oauth2.dto;

public interface OAuth2MemberInfo {
    String getProviderId(); //공급자 아이디
    String getProvider(); // 공급자
    String getName(); // 사용자 이름
    String getEmail();//사용자 이메일
}
