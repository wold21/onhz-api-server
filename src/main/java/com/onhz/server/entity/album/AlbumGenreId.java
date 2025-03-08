package com.onhz.server.entity.album;

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
public class AlbumGenreId implements Serializable {
    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "genre_id")
    private Long genreId;
}
