package com.moment.member.service;

import com.moment.entity.Member;
import com.moment.enums.Gender;
import com.moment.enums.Role;
import com.moment.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceImplTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Spy
    PasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void initUser(){
        Member saveMember = Member.builder()
                .email("test@test.com")
                .name("name1")
                .role(Role.ROLE_USER)
                .password(bCryptPasswordEncoder.encode("1234"))
                .birth(LocalDate.parse("1995-02-21"))
                .gender(Gender.MALE)
                .build();
        memberRepository.save(saveMember);
    }


    @Test
    void verifyEmail() throws Exception  {
        //given
        Optional<Member> byEmail = memberRepository.findByEmail("test@test.com");
        Assertions.assertThat(byEmail.get()).isNotNull();
        //when

        //then
    }
}