package com.onhz.server.entity.artist;

import com.onhz.server.entity.track.TrackEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_track_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistTrackEntity {

    @EmbeddedId
    private ArtistTrackId id;

    @MapsId("trackId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private TrackEntity track;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;
}
