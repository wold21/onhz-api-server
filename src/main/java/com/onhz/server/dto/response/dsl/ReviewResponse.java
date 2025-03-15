package com.onhz.server.dto.response.dsl;

import com.onhz.server.common.enums.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private UserResponse user;
    private String content;
    private Review review;
    private Long entityId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double rating;
    private int likeCount;
    private Boolean isLiked;
}
