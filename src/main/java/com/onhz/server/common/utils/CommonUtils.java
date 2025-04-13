package com.onhz.server.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommonUtils {
    public static String generateRandomString(int length) {
        Random random = new Random();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int start = random.nextInt(uuid.length() - length);
        String uuidPart = uuid.substring(start, start + length);
        return uuidPart;
    }
}
