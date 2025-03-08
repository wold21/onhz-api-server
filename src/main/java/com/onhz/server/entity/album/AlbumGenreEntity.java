package com.onhz.server.entity.album;

import com.onhz.server.entity.GenreEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "album_genre_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumGenreEntity {

    @EmbeddedId
    private AlbumGenreId id;

    @MapsId("albumId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private AlbumEntity album;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private GenreEntity genre;

    public void setAlbum(AlbumEntity album) {
        this.album = album;
    }
}
