package com.onhz.server.entity.album;

import com.onhz.server.entity.track.TrackEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(name = "release_date", nullable = false)
    private LocalDateTime releaseDate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "cover_path")
    private String coverPath;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackEntity> tracks = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlbumGenreEntity> albumGenres = new ArrayList<>();

    @OneToOne(mappedBy = "album", fetch = FetchType.LAZY)
    private AlbumRatingSummaryEntity ratingSummary;
}
