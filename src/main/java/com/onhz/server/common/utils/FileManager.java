package com.onhz.server.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
public class FileManager {

    public void uploadFile(Path savePath, InputStream inputStream) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)){
            Files.copy(bis, savePath);
        } catch (IOException e) {
            throw e;
        }
    }
    public Path createFolder(Path path) throws IOException {
        return Files.createDirectories(path);
    }
    public String getSaveFileName(String fileName) {
        int extensionIndex = fileName.lastIndexOf(".");
        return UUID.randomUUID() + fileName.substring(extensionIndex);
    }
    public void deleteFile(String filePath) {
        if(filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if(file.exists()) {
                file.delete();
            }
        }
        log.info("파일 삭제: {}", filePath);
    }
}
