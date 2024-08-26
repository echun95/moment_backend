package com.moment.member.controller;

import com.moment.common.dto.ResultDTO;
import com.moment.member.dto.JoinMemberDTO;
import com.moment.member.dto.LoginDTO;
import com.moment.member.dto.ReqEmailDTO;
import com.moment.member.dto.ReqMemberInfo;
import com.moment.member.service.MemberService;
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

    @PostMapping("/members/send-authentication-email")
    public ResponseEntity<ResultDTO> sendAuthenticationEmail(@Valid @RequestBody ReqEmailDTO reqEmailDTO) {
        memberService.sendAuthenticationEmail(reqEmailDTO);
        return new ResponseEntity<>(ResultDTO.of(10000, "이메일 인증 메일을 발송됐습니다.", null), HttpStatus.OK);
    }

    @GetMapping("/members/verify-email")
    public ResponseEntity<ResultDTO> verifyEmail(@NotBlank @Email @RequestParam(name = "email") String email,
                                                 @NotBlank @RequestParam(name = "code") String code) {
        ResultDTO<Object> response = memberService.verifyEmail(email, code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/members/join")
    public ResponseEntity<ResultDTO> join(@Valid @RequestBody JoinMemberDTO joinMemberDTO) {
        memberService.join(joinMemberDTO);
        return new ResponseEntity<>(ResultDTO.of(10000, "회원가입을 완료했습니다.", null), HttpStatus.OK);
    }
    @PostMapping("/members/login")
    public ResponseEntity<ResultDTO> login(@Valid @RequestBody LoginDTO.ReqLoginDTO loginDTO) {
        LoginDTO.ResLoginDTO resLoginDTO = memberService.login(loginDTO);
        return new ResponseEntity<>(ResultDTO.of(10000, "로그인을 완료했습니다.", resLoginDTO), HttpStatus.OK);
    }
    @PostMapping("/members/reset-password")
    public ResponseEntity<ResultDTO<Object>> resetPassword(@RequestParam String email) {
        memberService.resetPassword(email);
        return new ResponseEntity<>(ResultDTO.of(10000, "임시 비밀번호 발급을 완료했습니다.", null), HttpStatus.OK);
    }
    @GetMapping("/members/{memberId}")
    public ResponseEntity<ResultDTO> getMemberInfo(@PathVariable(name = "memberId") Long memberId){
        ReqMemberInfo memberInfo = memberService.getMemberInfo(memberId);
        return new ResponseEntity<>(ResultDTO.of(10000, "회원정보 조회를 완료했습니다.", memberInfo), HttpStatus.OK);
    }
    @PatchMapping("/members/{memberId}/password")
    public ResponseEntity<ResultDTO> modifyPassword(@PathVariable(name = "memberId") Long memberId,
                                                    @RequestBody String password){
        memberService.modifyPassword(memberId, password);
        return new ResponseEntity<>(ResultDTO.of(10000, "비밀번호를 수정했습니다.", null), HttpStatus.OK);
    }
    @PostMapping("/members/{memberId}/validate-password")
    public ResponseEntity<ResultDTO> validatePassword(@PathVariable(name = "memberId") Long memberId,
                                                      @RequestBody String password){
        memberService.validatePassword(memberId, password);
        return new ResponseEntity<>(ResultDTO.of(10000, "비밀번호 검증을 통과했습니다.", null), HttpStatus.OK);
    }
    @PostMapping("/members/{memberId}/profile")
    public ResponseEntity<ResultDTO> saveProfile(@PathVariable(name = "memberId") Long memberId,
                                                 @RequestParam(name = "profile") MultipartFile file){
        memberService.saveProfile(memberId, file);
        return new ResponseEntity<>(ResultDTO.of(10000, "프로필 저장을 완료했습니다.", null), HttpStatus.OK);
    }

}
