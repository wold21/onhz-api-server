package com.onhz.server.controller.auth;

import com.onhz.server.common.utils.RateLimiter;
import com.onhz.server.dto.request.EmailVerificationRequest;
import com.onhz.server.dto.request.LoginRequest;
import com.onhz.server.dto.request.SignUpRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.LoginResponse;
import com.onhz.server.dto.response.TokenResponse;
import com.onhz.server.dto.response.common.CommonResponse;
import com.onhz.server.dto.response.common.NoticeResponse;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.TooManyRequestException;
import com.onhz.server.service.auth.JwtTokenService;
import com.onhz.server.service.auth.VerificationService;
import com.onhz.server.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final RateLimiter rateLimiter;
    private final VerificationService verificationService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpDto) {
        userService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginDto, HttpServletRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = userService.login(loginDto, request, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "")
    public ResponseEntity<Void> logout(@RequestHeader("Device-Id") String deviceId, HttpServletRequest request) {
        userService.logout(deviceId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "access-token 갱신", description = "")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        TokenResponse token = jwtTokenService.refreshAccessToken(request, response);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "비밀번호 찾기", description = "")
    public ApiResponse<NoticeResponse> forgotPassword(
            HttpServletRequest request,
            @Parameter(description = "이메일", required = true, example = "example@example.com")
            @RequestParam String email,
            @Parameter(description = "닉네임", required = true, example = "onhz")
            @RequestParam String userName) {

        String clientIp = request.getRemoteAddr();
        if(!rateLimiter.allowRequest(clientIp, 3, 180)) {
            throw new TooManyRequestException(ErrorCode.TOO_MANY_REQUEST_EXCEPTION, "너무 많은 요청을 시도하였습니다. 잠시 후 다시 시도해 주세요.");
        }
        NoticeResponse result = userService.forgotPassword(email, userName);;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/email-verification")
    public ApiResponse<NoticeResponse> emailVerification(
            @RequestBody EmailVerificationRequest request,
            HttpServletRequest httpRequest){
        NoticeResponse results = verificationService.sendVerification(request, httpRequest);
        return ApiResponse.success(HttpStatus.OK, "success", results);
    }

    @PostMapping("/email-verification/verify")
    public ApiResponse<CommonResponse> emailVerify(
            @RequestBody EmailVerificationRequest request,
            HttpServletRequest httpRequest){
        CommonResponse results = verificationService.verifyEmail(request, httpRequest);
        return ApiResponse.success(HttpStatus.OK, "success", results);
    }
}
