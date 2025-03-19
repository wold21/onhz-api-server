package com.onhz.server.dto.response.artist;

import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.artist.ArtistEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ArtistTrackResponse extends ArtistResponse {
    private List<TrackResponse> tracks;

    @Builder(builderMethodName = "artistTrackBuilder")
    private ArtistTrackResponse(Long id, String name, String bio, String profilePath,
                                LocalDateTime createdAt, String country, List<TrackResponse> tracks) {
        super(id, name, bio, profilePath, createdAt, country);
        this.tracks = tracks;
    }
    public static ArtistTrackResponse of(ArtistEntity artist, List<TrackResponse> tracks) {
        return artistTrackBuilder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .profilePath(artist.getProfilePath())
                .createdAt(artist.getCreatedAt())
                .country(artist.getCountry())
                .tracks(tracks)
                .build();
    }
}
