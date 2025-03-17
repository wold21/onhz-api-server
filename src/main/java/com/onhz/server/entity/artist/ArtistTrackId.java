package com.onhz.server.entity.artist;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ArtistTrackId implements Serializable {
    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "track_id")
    private Long trackId;
}
