package com.onhz.server.entity.artist;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "artist_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "bio")
    private String bio;
    @Column(name = "profile_path")
    private String ProfilePath;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "country")
    private String country;
    @Column(nullable = false)
    private String mbId;

}
