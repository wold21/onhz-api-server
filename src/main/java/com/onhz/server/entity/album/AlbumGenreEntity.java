package com.onhz.server.entity.album;

import com.onhz.server.entity.GenreEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "album_genre_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumGenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumEntity album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private GenreEntity genre;

    public void setAlbum(AlbumEntity album) {
        this.album = album;
    }
}
