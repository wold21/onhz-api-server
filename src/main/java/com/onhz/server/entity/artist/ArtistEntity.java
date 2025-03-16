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
    @Column(name = "artist_name", nullable = false)
    private String name;
    @Column(name = "bio")
    private String bio;
    @Column(name = "profile_path")
    private String profilePath;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "country")
    private String country;
    @Column(name = "mbid", nullable = false)
    private String mbId;

}
