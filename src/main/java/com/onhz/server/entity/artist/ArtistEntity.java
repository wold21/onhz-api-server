package com.onhz.server.entity.artist;

import com.onhz.server.entity.album.AlbumEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistAlbumEntity> albumArtists = new ArrayList<>();

    public List<AlbumEntity> getAlbums() {
        return this.albumArtists.stream()
                .map(ArtistAlbumEntity::getAlbum)
                .collect(Collectors.toList());
    }

}
