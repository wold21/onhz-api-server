package com.onhz.server.entity.user;

import com.onhz.server.common.enums.Role;
import com.onhz.server.entity.SocialEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,updatable = false)
    private String email;

    @Column(nullable = false, unique = true,updatable = false)
    private String password;

    @Column(nullable = false, unique = true, name = "user_name")
    private String userName;

    @Column(nullable = false, name = "is_social")
    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_id")
    private SocialEntity social;

    @Column(name = "profile_path")
    private String profilePath;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(String email, String password, String userName, boolean isSocial, Role role) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.isSocial = isSocial;
        this.role = role;
    }

    @Builder(builderClassName = "OAuth2Builder", builderMethodName = "oauth2Builder")
    public UserEntity(String email, String userName, String password, SocialEntity social, String profilePath) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.isSocial = true;
        this.role = Role.USER;
        this.social = social;
        this.profilePath = profilePath;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateUserName(String userName) { this.userName = userName;}

}
