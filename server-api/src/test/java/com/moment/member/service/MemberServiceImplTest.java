package com.moment.member.service;

import com.moment.couple.repository.CoupleRepository;
import com.moment.entity.Couple;
import com.moment.entity.Member;
import com.moment.enums.Gender;
import com.moment.enums.Role;
import com.moment.member.dto.ResMemberInfo;
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
class MemberServiceImplTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CoupleRepository coupleRepository;
    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    @Transactional
    void initCouple(){
        Couple couple = Couple.builder()
                .meetingDate(LocalDate.parse("2024-10-10"))
                .build();
        coupleRepository.save(couple);
        Member maleMember = Member.builder()
                .email("male@test.com")
                .name("male")
                .role(Role.ROLE_USER)
                .password(bCryptPasswordEncoder.encode("1234"))
                .birth(LocalDate.parse("1995-02-21"))
                .gender(Gender.MALE)
                .build();
        maleMember.createCouple(couple);
        memberRepository.save(maleMember);

        Member femaleMember = Member.builder()
                .email("female@test.com")
                .name("female")
                .role(Role.ROLE_USER)
                .password(bCryptPasswordEncoder.encode("1234"))
                .birth(LocalDate.parse("1995-06-22"))
                .gender(Gender.MALE)
                .build();
        femaleMember.createCouple(couple);
        memberRepository.save(femaleMember);
    }


    @Test
    void verifyEmail() throws Exception  {
        //given
        Optional<Member> byEmail = memberRepository.findByEmail("male@test.com");
        //when
        //then
        Assertions.assertThat(byEmail.get()).isNotNull();
    }

    @Test
    void getMemberInfo(){
        ResMemberInfo memberInfo = memberService.getMemberInfo(1L);
        Assertions.assertThat(memberInfo.getIsCouple()).isTrue();
    }


}