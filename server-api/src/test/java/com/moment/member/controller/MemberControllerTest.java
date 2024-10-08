package com.moment.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.common.exception.RestApiException;
import com.moment.common.exception.member.MemberErrorCode;
import com.moment.enums.Gender;
import com.moment.enums.Role;
import com.moment.member.dto.JoinMemberDTO;
import com.moment.member.dto.LoginDTO;
import com.moment.member.dto.ReqEmailDTO;
import com.moment.member.dto.ReqPasswordDTO;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.FileInputStream;
import java.security.Key;
import java.time.LocalDate;
import java.util.Date;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;

import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("내 정보 조회 성공",
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
		Mockito.when(memberService.getMemberInfo(Mockito.any()))
			.thenThrow(new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));

		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/memberInfo")
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("존재하지 않는 계정 오류",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("인증 메일 발송 api")
	void sendAuthenticationEmail() throws Exception {
		Mockito.doNothing().when(memberService).sendAuthenticationEmail(Mockito.any());
		String content = objectMapper.writeValueAsString(new ReqEmailDTO("test@gmail.com"));

		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/send-authentication-email")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("인증 메일 발송 성공",
				requestFields(
					fieldWithPath("email").description("email")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.ALREADY_BEEN_JOIN))
			.when(memberService)
			.sendAuthenticationEmail(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/send-authentication-email")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("이미 회원가입이 되어있는 계정 오류",
				requestFields(
					fieldWithPath("email").description("email")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.FAILED_SEND_AUTHENTICATION_EMAIL))
			.when(memberService)
			.sendAuthenticationEmail(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/send-authentication-email")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("인증 메일 발송 오류",
				requestFields(
					fieldWithPath("email").description("email")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("이메일 인증 api")
	void verifyEmail() throws Exception {
		Mockito.doNothing().when(memberService).verifyEmail(Mockito.any(), Mockito.any());
		MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("email", "test@gmail.com");
		requestParams.add("code", "CODE123");
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/verify-email")
				.params(requestParams)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("이메일 인증 성공",
				queryParameters(
					parameterWithName("email").description("사용자 이메일"),
					parameterWithName("code").description("인증 코드")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.FAILED_VERIFY_EMAIL))
			.when(memberService)
			.verifyEmail(Mockito.any(), Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/verify-email")
				.params(requestParams)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("이메일 인증 오류",
				queryParameters(
					parameterWithName("email").description("사용자 이메일"),
					parameterWithName("code").description("인증 코드")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("회원가입 api")
	void join() throws Exception {
		Mockito.doNothing().when(memberService).join(Mockito.any());
		String content = objectMapper.writeValueAsString(
			new JoinMemberDTO("test@gmail.com", "password", "testName", Gender.MALE, LocalDate.now()));
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/join")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("회원가입 성공",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.DUPLICATE_MEMBER)).when(memberService).join(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/join")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("이미 회원가입이 된 계정 오류",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("로그인 api")
	void login() throws Exception {
		Mockito.when(memberService.login(Mockito.any())).thenReturn(LoginDTO.ResLoginDTO.builder()
			.accessToken("accessJWT")
			.userRole(Role.ROLE_USER)
			.userName("userName")
			.tempPasswordActive(false)
			.build());
		String content = objectMapper.writeValueAsString(new LoginDTO.ReqLoginDTO("test@gmail.com", "password"));
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("로그인 성공",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data.accessToken").description("로그인 성공 시 응답되는 JWT"),
					fieldWithPath("data.userRole").description("유저 권한"),
					fieldWithPath("data.userName").description("유저 이름"),
					fieldWithPath("data.tempPasswordActive").description("임시 비밀번호 사용 유무")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER))
			.when(memberService)
			.login(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("회원가입 되어있지 않은 계정 오류",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.FAILED_AUTHENTICATION_TEMPORARY_PASSWORD))
			.when(memberService)
			.login(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("임시 비밀번호 오류",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.FAILED_VALIDATE_PASSWORD))
			.when(memberService)
			.login(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/login")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("비밀번호 오류",
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("임시 비밀번호 발급 api")
	void resetPassword() throws Exception {
		Mockito.doNothing().when(memberService).resetPassword(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/resetPassword")
				.param("email", "test@gmail.com")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("임시 비밀번호 발급 성공",
				queryParameters(
					parameterWithName("email").description("사용자 이메일")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));

		Mockito.doThrow(new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER))
			.when(memberService)
			.resetPassword(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/resetPassword")
				.param("email", "test@gmail.com")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("회원가입이 되어있지않은 계정 오류",
				queryParameters(
					parameterWithName("email").description("사용자 이메일")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.FAILED_SEND_TEMPORARY_PASSWORD_EMAIL))
			.when(memberService)
			.resetPassword(Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/resetPassword")
				.param("email", "test@gmail.com")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("임시 비밀번호 발송 실패 오류",
				queryParameters(
					parameterWithName("email").description("사용자 이메일")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("비밀번호 변경 api")
	void modifyPassword() throws Exception {
		Mockito.doNothing().when(memberService).modifyPassword(Mockito.any(), Mockito.any());
		String content = objectMapper.writeValueAsString(new ReqPasswordDTO("passWord"));
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/auth/password")
				.header("X-GATEWAY-AUTH-HEADER", 100L)
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("비밀번호 변경 성공",
				requestFields(
					fieldWithPath("password").description("패스워드")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER))
			.when(memberService)
			.modifyPassword(Mockito.any(), Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/auth/password")
				.header("X-GATEWAY-AUTH-HEADER", 100L)
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("회원가입이 되어있지않은 계정 오류",
				requestFields(
					fieldWithPath("password").description("패스워드")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("비밀번호 검증 api")
	void validatePassword() throws Exception {
		Mockito.doNothing().when(memberService).validatePassword(Mockito.any(), Mockito.any());
		String content = objectMapper.writeValueAsString(new ReqPasswordDTO("password"));
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/validate-password")
				.header("X-GATEWAY-AUTH-HEADER", 100L)
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("비밀번호 검증 성공",
				requestFields(
					fieldWithPath("password").description("패스워드")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지"),
					fieldWithPath("data").description("응답 데이터")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER))
			.when(memberService)
			.validatePassword(Mockito.any(), Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/validate-password")
				.header("X-GATEWAY-AUTH-HEADER", 100L)
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("비밀번호 검증 성공",
				requestFields(
					fieldWithPath("password").description("회원가입이 되어있지않은 계정 오류")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
		Mockito.doThrow(new RestApiException(MemberErrorCode.FAILED_VALIDATE_PASSWORD))
			.when(memberService)
			.validatePassword(Mockito.any(), Mockito.any());
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/validate-password")
				.header("X-GATEWAY-AUTH-HEADER", 100L)
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("비밀번호 검증 성공",
				requestFields(
					fieldWithPath("password").description("잘못된 비밀번호 오류")
				),
				responseFields(
					fieldWithPath("code").description("응답 코드"),
					fieldWithPath("message").description("응답 메시지")
				)
			));
	}

	@Test
	@DisplayName("프로필 이미지 저장 api")
	void saveProfile() throws Exception {
		FileInputStream fileInputStream = new FileInputStream("");
		MockMultipartFile mockMultipartFile = new MockMultipartFile("testImage", "test.png", "png", fileInputStream);
		Mockito.doNothing().when(memberService).saveProfile(Mockito.any(), Mockito.any());

		mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/v1/auth/profile")
				.file(mockMultipartFile)
				.header("X-GATEWAY-AUTH-HEADER", 100L)
				.header("Authorization",
					"Bearer " + createJwt("dldydcns123@gmail.com", accessTokenExpirationMs)) // JWT 토큰을 포함
				.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(document("프로필 저장 성공",
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
