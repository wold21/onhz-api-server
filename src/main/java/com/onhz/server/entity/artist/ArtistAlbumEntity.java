package com.onhz.server.entity.artist;

import com.onhz.server.entity.album.AlbumEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_album_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistAlbumEntity {
    @EmbeddedId
    private ArtistAlbumId id;

    @MapsId("albumId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumEntity album;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @Column(name="artist_role")
    private String artistRole;
}
