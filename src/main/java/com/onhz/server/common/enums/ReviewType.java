package com.onhz.server.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReviewType {
    ARTIST("artist"), ALBUM("album"), TRACK("track");
    private final String key;
}
