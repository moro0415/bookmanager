package com.changhak.bookmanager.util;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {

    // 절대 경로: 프로젝트 루트 기준
    private final String uploadDir = System.getProperty("user.dir") + "/upload/";
    private final String thumbnailDir = System.getProperty("user.dir") + "/upload/thumb/";

    // 실행 시 디렉토리 자동 생성
    @PostConstruct
    public void init() {
        new File(uploadDir).mkdirs();
        new File(thumbnailDir).mkdirs();
    }

    public String storeFile(MultipartFile file, boolean makeThumbnail) throws IOException {
        if(file.isEmpty()){
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일 이름이 null입니다.");
        }

        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String storedFilename = uuid + "." + ext;

        File originalFile = new File(uploadDir + storedFilename);
        file.transferTo(originalFile);

        if(makeThumbnail){
            String thumbnailPath = thumbnailDir + storedFilename;
            createThumbnail(originalFile, thumbnailPath);
        }

        return storedFilename;
    }

    public void createThumbnail(File originalFile, String thumbnailPath) throws IOException {
        Thumbnails.of(originalFile)
                .size(200, 300)
                .toFile(thumbnailPath);

    }

    public void deleteFile(String path){

        if (path == null) return;

        File file = new File(path);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("파일 삭제 실패: " + file.getAbsolutePath());
        }

    }

    private String extractExt(String filename) {
        int pos = filename.lastIndexOf(".");
        if (pos == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다: " + filename);
        }
        return filename.substring(pos + 1);
    }

}
