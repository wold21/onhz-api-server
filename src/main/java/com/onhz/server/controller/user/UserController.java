package com.onhz.server.controller.user;


import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.request.PasswordChangeRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.UserExistsResponse;
import com.onhz.server.dto.response.review.ReviewResponse;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{userId}/reviews/{reviewType}")
    @Operation(summary = "내가 남긴 리뷰 리스트 (아티스트/앨범/트랙)", description = "")
    public ApiResponse<List<ReviewResponse>> getUserReviews(
            @PathVariable(name="userId") Long userId,
            @Parameter(description = "리뷰 유형",
                    schema = @Schema(implementation = ReviewType.class))
            @PathVariable(name="reviewType") ReviewType reviewType,
            @Parameter(description = "리뷰 대상 ID (album_id or artist_id or track_id)")
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {
        List<ReviewResponse> result = userService.getUserReviews(userId, reviewType, offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
