package com.onhz.server.entity.track;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.artist.ArtistTrackEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "track_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "track_name")
    private String trackName;
    @Column(name = "track_rank")
    private int trackRank;
    @Column
    private int duration;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private AlbumEntity album;

    @OneToMany(mappedBy = "track", fetch = FetchType.LAZY)
    private List<ArtistTrackEntity> artists = new ArrayList<>();
}
