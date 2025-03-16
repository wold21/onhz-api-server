package com.onhz.server.controller.auth;

import com.onhz.server.dto.request.LoginRequest;
import com.onhz.server.dto.request.SignUpRequest;
import com.onhz.server.dto.request.TokenRefreshRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.LoginResponse;
import com.onhz.server.dto.response.TokenResponse;
import com.onhz.server.service.auth.JwtTokenService;
import com.onhz.server.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpDto) {
        userService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginDto, HttpServletRequest request) {
        LoginResponse loginResponse = userService.login(loginDto, request);
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
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest refreshDto) {
        TokenResponse token = jwtTokenService.refreshAccessToken(refreshDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success(HttpStatus.OK, "success", "pong");
    }
}
