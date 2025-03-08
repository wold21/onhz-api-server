package com.onhz.server.controller.user;


import com.onhz.server.dto.request.PasswordChangeRequest;
import com.onhz.server.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal String email, @Valid @RequestBody PasswordChangeRequest requestDto) {
        userService.changePassword(email, requestDto);
        return ResponseEntity.ok().build();
    }
}
