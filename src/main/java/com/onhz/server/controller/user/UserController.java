package com.onhz.server.controller.user;


import com.onhz.server.dto.request.PasswordChangeRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.UserExistsResponse;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserEntity user, @Valid @RequestBody PasswordChangeRequest requestDto) {
        userService.changePassword(user.getEmail(), requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/username/{userName}")
    public ApiResponse<UserExistsResponse> userNameCheck(
            @PathVariable String userName
    ) {
        UserExistsResponse result = userService.nameCheck(userName);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/exists/email/{email}")
    public ApiResponse<UserExistsResponse> userEmailCheck(
            @PathVariable String email
    ) {
        UserExistsResponse result = userService.emailCheck(email);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
