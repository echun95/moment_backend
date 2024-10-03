package com.moment.member.controller;

import com.moment.common.dto.LoginMemberInfo;
import com.moment.common.dto.ResultDTO;
import com.moment.member.dto.*;
import com.moment.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
@Validated
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/auth/send-authentication-email")
    @Operation(summary = "인증 메일 발송 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResultDTO.class))}),
            @ApiResponse(responseCode = "10001", description = "인증 메일 발송을 실패했습니다. 다시 시도해주세요."),
            @ApiResponse(responseCode = "10010", description = "이미 회원가입이 되어있는 계정입니다.")
    })
    public ResponseEntity<ResultDTO> sendAuthenticationEmail(@Valid @RequestBody ReqEmailDTO reqEmailDTO) {
        memberService.sendAuthenticationEmail(reqEmailDTO);
        return new ResponseEntity<>(ResultDTO.of(10000, "이메일 인증 메일을 발송됐습니다.", null), HttpStatus.OK);
    }

    @GetMapping("/auth/verify-email")
    @Operation(summary = "이메일 인증 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "10002", description = "잘못된 인증 코드입니다. 인증 코드를 확인해주세요.")
    })
    public ResponseEntity<ResultDTO> verifyEmail(@NotBlank @Email @RequestParam(name = "email") String email,
                                                 @NotBlank @RequestParam(name = "code") String code) {
        memberService.verifyEmail(email, code);
        return new ResponseEntity<>(ResultDTO.of(10000, "이메일 인증을 완료했습니다.", null), HttpStatus.OK);
    }

    @PostMapping("/auth/join")
    @Operation(summary = "회원가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "10003", description = "이미 회원가입이 되어있는 계정입니다.")
    })
    public ResponseEntity<ResultDTO> join(@Valid @RequestBody JoinMemberDTO joinMemberDTO) {
        memberService.join(joinMemberDTO);
        return new ResponseEntity<>(ResultDTO.of(10000, "회원가입을 완료했습니다.", null), HttpStatus.OK);
    }
    @PostMapping("/auth/login")
    @Operation(summary = "로그인 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginDTO.ResLoginDTO.class))}),
            @ApiResponse(responseCode = "10004", description = "로그인에 실패했습니다. 이메일 또는 비밀번호를 다시 확인해주세요."),
            @ApiResponse(responseCode = "10007", description = "임시 비밀번호를 다시 확인해주세요."),
            @ApiResponse(responseCode = "10008", description = "잘못된 비밀번호입니다. 비밀번호를 다시 확인해주세요."),
    })
    public ResponseEntity<ResultDTO> login(@Valid @RequestBody LoginDTO.ReqLoginDTO loginDTO) {
        LoginDTO.ResLoginDTO resLoginDTO = memberService.login(loginDTO);
        return new ResponseEntity<>(ResultDTO.of(10000, "로그인을 완료했습니다.", resLoginDTO), HttpStatus.OK);
    }
    @PostMapping("/auth/reset-password")
    @Operation(summary = "임시 비밀번호 발급 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "10005", description = "회원가입이 되어있지않은 계정입니다."),
            @ApiResponse(responseCode = "10006", description = "인증 메일 발송을 실패했습니다. 다시 시도해주세요."),
    })
    public ResponseEntity<ResultDTO> resetPassword(@RequestParam(name = "email") String email) {
        memberService.resetPassword(email);
        return new ResponseEntity<>(ResultDTO.of(10000, "임시 비밀번호 발급을 완료했습니다.", null), HttpStatus.OK);
    }
    @GetMapping("/memberInfo")
    @Operation(summary = "내정보 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResMemberInfo.class))}),
            @ApiResponse(responseCode = "10005", description = "회원가입이 되어있지않은 계정입니다."),
    })
    public ResponseEntity<ResultDTO> getMemberInfo(@Parameter(hidden = true) LoginMemberInfo loginMemberInfo){
        ResMemberInfo memberInfo = memberService.getMemberInfo(loginMemberInfo.getMemberId());
        return new ResponseEntity<>(ResultDTO.of(10000, "회원정보 조회를 완료했습니다.", memberInfo), HttpStatus.OK);
    }
    @PatchMapping("/auth/password")
    @Operation(summary = "비밀번호 변경 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "10005", description = "회원가입이 되어있지않은 계정입니다."),
    })
    public ResponseEntity<ResultDTO> modifyPassword(@Parameter(hidden = true) LoginMemberInfo loginMemberInfo,
                                                    @RequestBody ReqPasswordDTO passwordDTO){
        memberService.modifyPassword(loginMemberInfo.getMemberId(), passwordDTO.getPassword());
        return new ResponseEntity<>(ResultDTO.of(10000, "비밀번호를 수정했습니다.", null), HttpStatus.OK);
    }
    @PostMapping("/auth/validate-password")
    @Operation(summary = "비밀번호 검증 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "10005", description = "회원가입이 되어있지않은 계정입니다."),
            @ApiResponse(responseCode = "10008", description = "잘못된 비밀번호입니다. 비밀번호를 다시 확인해주세요."),
    })
    public ResponseEntity<ResultDTO> validatePassword(@Parameter(hidden = true) LoginMemberInfo loginMemberInfo,
                                                      @RequestBody ReqPasswordDTO passwordDTO){
        memberService.validatePassword(loginMemberInfo.getMemberId(), passwordDTO.getPassword());
        return new ResponseEntity<>(ResultDTO.of(10000, "비밀번호 검증을 통과했습니다.", null), HttpStatus.OK);
    }
    @PostMapping("/auth/profile")
    @Operation(summary = "프로필 이미지 저장 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "10000", description = "Successful",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "multipart/form-data")}),
            @ApiResponse(responseCode = "10005", description = "회원가입이 되어있지않은 계정입니다."),
            @ApiResponse(responseCode = "10009", description = "프로필 저장을 실패했습니다. 다시 시도해주세요."),
    })
    public ResponseEntity<ResultDTO> saveProfile(@Parameter(hidden = true) LoginMemberInfo loginMemberInfo,
                                                 @RequestPart(name = "profile") MultipartFile file){
        memberService.saveProfile(loginMemberInfo.getMemberId(), file);
        return new ResponseEntity<>(ResultDTO.of(10000, "프로필 저장을 완료했습니다.", null), HttpStatus.OK);
    }

}
