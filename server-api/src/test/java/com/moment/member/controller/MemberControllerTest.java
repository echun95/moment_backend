package com.moment.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.common.exception.RestApiException;
import com.moment.common.exception.member.MemberErrorCode;
import com.moment.enums.Gender;
import com.moment.member.dto.ResMemberInfo;
import com.moment.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Key;
import java.time.LocalDate;
import java.util.Date;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;


    private String secretKey = "bW9tZW50and0c2VjcmV0IUA=bW9tZW50and0c2VjcmV0IUA=bW9tZW50and0c2VjcmV0IUA=bW9tZW50and0c2VjcmV0IUA=";
    private String accessTokenExpirationMs = "300000000";
    private String refreshTokenExpirationMs = "600000";

    @Test
    @DisplayName("내 정보 조회")
    void getMemberInfo() throws Exception {
        //given
        Mockito.when(memberService.getMemberInfo(Mockito.any())).thenReturn(
                ResMemberInfo.builder()
                        .memberId(100L)
                        .email("dldydcns123@gmail.com")
                        .name("이도현")
                        .phoneNumber("01064384692")
                        .gender(Gender.MALE)
                        .birth(LocalDate.now())
                        .userCode("CAB@232")
                        .isCouple(false)
                        .build()
        );
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/memberInfo")
                        .header("X-GATEWAY-AUTH-HEADER", 100L)
                        .header("Authorization", "Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("getMemberInfo",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.memberId").description("사용자 고유 ID"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.email").description("사용자 Email"),
                                fieldWithPath("data.phoneNumber").description("사용자 연락처"),
                                fieldWithPath("data.gender").description("사용자 성별"),
                                fieldWithPath("data.profileImageUrl").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.birth").description("사용자 생일"),
                                fieldWithPath("data.userCode").description("사용자 코드"),
                                fieldWithPath("data.connectionUserCode").description("연결된 사용자 코드"),
                                fieldWithPath("data.isCouple").description("커플 여부")
                        )
                ))
        ;
        // 사용자가 존재하지 않을 때의 테스트
        Mockito.when(memberService.getMemberInfo(Mockito.any())).thenThrow(new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/memberInfo")
                        .header("Authorization", "Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("getMemberInfo-not-found",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    private String createJwt(String userEmail, String tokenExpirationMs) {
        // Claims 란 JWT의 payload 부분에 들어가는 데이터 단위라고 보면 된다.
        // Map<String, Object>를 상속하고 있기 때문에 key, value 형식으로 값을 넣을 수 있다.
        Claims claims = Jwts.claims().setSubject(userEmail); // username

        // 토큰 생성 시간
        Date now = new Date();
        // 토큰 만료 시간
        Date expireDate = new Date(now.getTime() + Long.parseLong(tokenExpirationMs));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
