package com.onhz.server.entity;

import com.onhz.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "social_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialEntity implements CodeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "social", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserEntity> users = new ArrayList<>();

    @Builder
    public SocialEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

