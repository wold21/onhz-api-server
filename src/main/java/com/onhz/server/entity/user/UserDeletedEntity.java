package com.onhz.server.entity.user;

import com.onhz.server.common.enums.Role;
import com.onhz.server.entity.SocialEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tb_del")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDeletedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name")
    private String userName;
    @Column
    private String email;
    @Column(name = "deleted_at")
    @CreationTimestamp
    private LocalDateTime deletedAt;
    @Column(name = "is_social")
    private boolean isSocial;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_id")
    private SocialEntity social;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    public static UserDeletedEntity fromUser(UserEntity user) {
        UserDeletedEntity deletedUser = new UserDeletedEntity();
        deletedUser.userName = user.getUserName();
        deletedUser.email = user.getEmail();
        deletedUser.isSocial = user.isSocial();
        deletedUser.social = user.getSocial();
        deletedUser.role = user.getRole();
        return deletedUser;
    }
}
