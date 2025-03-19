package com.onhz.server.dto.response.artist;

import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.entity.artist.ArtistEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ArtistAlbumResponse extends ArtistResponse {
    private List<AlbumResponse> albums;

    @Builder(builderMethodName = "artistTrackBuilder")
    private ArtistAlbumResponse(Long id, String name, String bio, String profilePath,
                                LocalDateTime createdAt, String country, List<AlbumResponse> albums) {
        super(id, name, bio, profilePath, createdAt, country);
        this.albums = albums;
    }
    public static ArtistAlbumResponse of(ArtistEntity artist, List<AlbumResponse> albums) {
        return artistTrackBuilder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .profilePath(artist.getProfilePath())
                .createdAt(artist.getCreatedAt())
                .country(artist.getCountry())
                .albums(albums)
                .build();
    }
}
