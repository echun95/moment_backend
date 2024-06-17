package com.moment.config.oauth2.dto;

import java.util.Map;

public class KakaoMemberInfo implements  OAuth2MemberInfo{
    private Map<String, Object> attributes;
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;

    public KakaoMemberInfo(Map<String, Object> attributes) {
//        {id=아이디값,
//                connected_at=2022-02-22T15:50:21Z,
//                properties={nickname=이름},
//                kakao_account={
//                        profile_nickname_needs_agreement=false,
//                        profile={nickname=이름},
//                        has_email=true,
//                        email_needs_agreement=false,
//                        is_email_valid=true,
//                        is_email_verified=true,
//                        email=이메일}
//        }
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return attributesProfile.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return attributesAccount.get("email").toString();
    }
}
