package com.onhz.server.controller.auth;

import com.onhz.server.dto.LoginRequestDto;
import com.onhz.server.dto.SignUpRequestDto;
import com.onhz.server.dto.TokenRefreshRequestDto;
import com.onhz.server.dto.TokenResponseDto;
import com.onhz.server.service.auth.JwtTokenService;
import com.onhz.server.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequestDto signUpDto) {
        userService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto, HttpServletRequest request) {
        TokenResponseDto token = userService.login(loginDto, request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Device-Id") String deviceId, HttpServletRequest request) {
        userService.logout(deviceId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@Valid @RequestBody TokenRefreshRequestDto refreshDto) {
        TokenResponseDto token = jwtTokenService.refreshAccessToken(refreshDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
