package com.moment.entity;

import com.moment.enums.Gender;
import com.moment.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER")
    private Gender gender;

    @Column(name = "PROFILE_IMAGE_URL")
    private Gender profileImageUrl;

    @Column(name = "BIRTH")
    private LocalDate birth;

    @Column(name = "USER_CODE")
    private String userCode;

    @Column(name = "CONNECTION_USER_CODE")
    private String connectionUserCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @Column(name = "PROVIDER")
    private String provider;
    @Column(name = "PROVIDER_ID")
    private String providerId;
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @Builder
    public Member(Long memberId, String name, String password, String email, String phoneNumber, Gender gender, Gender profileImageUrl, LocalDate birth, String userCode, String connectionUserCode, Role role, String provider, String providerId, String refreshToken) {
        this.memberId = memberId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.birth = birth;
        this.userCode = userCode;
        this.connectionUserCode = connectionUserCode;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.refreshToken = refreshToken;
    }






    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
