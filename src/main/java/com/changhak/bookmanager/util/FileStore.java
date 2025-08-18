package com.changhak.bookmanager.util;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 파일 저장소 유틸리티 클래스
 * - 업로드된 파일을 서버 로컬 디렉토리에 저장
 * - UUID 기반의 고유 파일명 생성
 * - Thumbnailator 라이브러리를 이용한 썸네일 생성
 * - 저장된 파일 삭제 기능 지원

 /**
 * 저장 경로는 프로젝트 루트 기준으로 /upload, /upload/thumb 디렉토리이며
 * 애플리케이션 실행 시 자동 생성된다
 */

@Component
public class FileStore {

    // 업로드 경로 (절대 경로: 프로젝트 루트 기준)
    private final String uploadDir = System.getProperty("user.dir") + "/upload/";
    private final String thumbnailDir = System.getProperty("user.dir") + "/upload/thumb/";

    // 애플리케이션 실행 시 업로드/썸네일 디렉토리 자동 생성
    @PostConstruct
    public void init() {
        new File(uploadDir).mkdirs();
        new File(thumbnailDir).mkdirs();
    }

    /*** 파일 저장 처리 */
    public String storeFile(MultipartFile file, boolean makeThumbnail) throws IOException {
        if(file.isEmpty()){
            return null;
        }

        // 원본 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일 이름이 null입니다.");
        }

        // 확장자 추출 및 UUID 기반 저장 파일명 생성
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String storedFilename = uuid + "." + ext;

        // 실제 파일 저장 (upload 디렉토리)
        File originalFile = new File(uploadDir + storedFilename);
        file.transferTo(originalFile);

        // makeThumbnail이 true인 경우 썸네일 생성 (즉, 요청 시)
        if(makeThumbnail){
            String thumbnailPath = thumbnailDir + storedFilename;
            createThumbnail(originalFile, thumbnailPath);
        }

        //UUID 기반 파일명 반환
        return storedFilename;
    }

    /** 썸네일 생성 */
    private void createThumbnail(File originalFile, String thumbnailPath) throws IOException {
        Thumbnails.of(originalFile)
                .size(200, 300)
                .toFile(thumbnailPath);

    }
    /** 파일 삭제 */
    public void deleteFile(String path){

        if (path == null) return;

        File file = new File(path);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("파일 삭제 실패: " + file.getAbsolutePath());
        }

    }

    /** 확장자 추출 */
    private String extractExt(String filename) {
        int pos = filename.lastIndexOf(".");
        if (pos == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다: " + filename);
        }
        return filename.substring(pos + 1);
    }

}
