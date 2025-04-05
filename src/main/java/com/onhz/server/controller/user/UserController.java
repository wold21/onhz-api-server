package com.onhz.server.controller.user;


import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.request.UserInfoRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.SummaryResponse;
import com.onhz.server.dto.response.UserExistsResponse;
import com.onhz.server.dto.response.UserResponse;
import com.onhz.server.dto.response.review.ReviewResponse;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/profile")
    @Operation(summary = "사용자 정보 변경", description = "")
    public ApiResponse<UserResponse> changeUserInfo(
            @AuthenticationPrincipal UserEntity user,
            @Valid @RequestBody UserInfoRequest requestDto) {
        UserResponse result = userService.changeUserInfo(user, requestDto);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping ("/profile-image")
    @Operation(summary = "프로필 이미지 변경", description = "")
    public ApiResponse<UserResponse> changeUserImage(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserResponse result = userService.changeUserImage(user, file);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @DeleteMapping
    @Operation(summary = "유저 삭제", description = "")
    public ApiResponse deleteUser(
            @AuthenticationPrincipal UserEntity user
    ) {
        userService.deleteUser(user);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }
    @DeleteMapping("/{userId}")
    @Operation(summary = "유저 삭제(Id 사용)", description = "")
    public ApiResponse deleteUserById(
            @PathVariable(name="userId") Long userId) {
        userService.deleteUserById(userId);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @GetMapping("/exists/username/{userName}")
    @Operation(summary = "닉네임 중복 체크", description = "")
    public ApiResponse<UserExistsResponse> userNameCheck(
            @PathVariable(name="userName") String userName
    ) {
        UserExistsResponse result = userService.nameCheck(userName);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "이메일 중복 체크", description = "")
    public ApiResponse<UserExistsResponse> userEmailCheck(
            @PathVariable(name="email") String email
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
            @Parameter(description = "이전 페이지 마지막 데이터의 ID 값\n * 첫번째 페이지, lastId = null")
            @RequestParam(name = "lastId", required = false) Long lastId,
            @Parameter(description = "이전 페이지 마지막 데이터의 orderBy 로 설정된 값\n * 첫번째 페이지, lastOrderValue = null ")
            @RequestParam(name = "lastOrderValue", required = false) String lastOrderValue,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "createdAt") String orderBy) {
        List<ReviewResponse> result = userService.getUserReviews(userId, reviewType, lastId, lastOrderValue, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{userId}/ratings")
    @Operation(summary = "나의 평균 평점 및 평점 분포도", description = "")
    public ApiResponse<SummaryResponse> getUserRatings(
            @PathVariable(name="userId") Long userId
    ) {
        SummaryResponse result = userService.getUserRatings(userId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
