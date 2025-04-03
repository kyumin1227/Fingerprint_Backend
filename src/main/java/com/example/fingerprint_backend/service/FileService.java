package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.exception.FileException;
import com.example.fingerprint_backend.types.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class FileService {

    @Value("${cloud.aws.s3.image-bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * 파일을 S3에 업로드하는 메소드
     *
     * @param fileType - 파일 형식
     * @param file     - 업로드할 파일
     * @param path     - S3에 저장할 경로 (예: "images/profile/{studentNumber}")
     * @param maxSize  - 최대 파일 크기 (바이트 단위)
     * @return - 업로드된 파일의 URL
     */
    public String storeFile(FileType fileType, MultipartFile file, String path, long maxSize) {
        validateFileSize(file, maxSize);
        validateFileFormat(file.getOriginalFilename(), fileType);
        String fileName = generateFileName(fileType.getExtensions().iterator().next());
        System.out.println("fileName = " + fileName);
        uploadFile(file, path + fileName);

        return fileName;
    }

    public void uploadFile(MultipartFile file, String key) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName) // TODO : 나중에 버킷이 추가 되면 확장자에 맞춰 버킷 이름을 변경해야 함
                .key(key)
                .contentType(file.getContentType())
                .contentDisposition("inline")
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            throw new FileException("파일 업로드에 실패했습니다.");
        }
    }

    /**
     * 파일 이름을 생성하는 메소드
     *
     * @param extension - 파일 확장자
     * @return - 생성된 파일 이름 (형식: /timestamp_uuid.extension)
     */
    public String generateFileName(String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString();
        return String.format("/%s_%s.%s", timestamp, uuid, extension);
    }

    /**
     * 파일의 크기를 검증하는 메소드
     *
     * @param file    - 확인할 파일
     * @param maxSize - 최대 크기
     * @throws FileException - 파일 크기가 너무 큰 경우
     */
    public void validateFileSize(MultipartFile file, long maxSize) {
        if (file.getSize() > maxSize) {
            throw new FileException(String.format(
                    "파일 크기가 너무 큽니다. (최대: %.2f MB, 업로드: %.2f MB)",
                    maxSize / 1024.0 / 1024.0,
                    file.getSize() / 1024.0 / 1024.0
            ));
        }
    }

    /**
     * 파일의 형식을 검증하는 메소드
     *
     * @param fileName - 확인할 파일 이름
     * @param fileType - 확인할 파일 형식
     * @throws FileException - 파일 형식이 잘못된 경우
     */
    public void validateFileFormat(String fileName, FileType fileType) {
        for (String extension : fileType.getExtensions()) {
            if (fileName.toLowerCase().endsWith("." + extension)) {
                return;
            }
        }
        throw new FileException(String.format(
                "파일 형식이 잘못되었습니다. (허용된 형식: %s)",
                String.join(", ", fileType.getExtensions())
        ));
    }


}
