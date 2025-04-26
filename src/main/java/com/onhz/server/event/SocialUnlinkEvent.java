package com.onhz.server.event;

import com.onhz.server.entity.user.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SocialUnlinkEvent extends ApplicationEvent {
    private final UserEntity user;
    private final boolean isSocialUnlink;

    public SocialUnlinkEvent(Object source, UserEntity user, boolean isSocialUnlink) {
        super(source);
        this.user = user;
        this.isSocialUnlink = isSocialUnlink;
    }
}
