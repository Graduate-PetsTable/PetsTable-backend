package com.example.petstable.global.support;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.petstable.global.support.UploadMessage.*;

@Component
@RequiredArgsConstructor
public class AwsS3Uploader {

    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp", "image/heic", "image/heif");
    private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L; // 최대 5MB

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(String directoryPath, MultipartFile multipartFile) {
        String fileName = directoryPath + generateImageFileName(multipartFile);  // 이미지 파일 이름 생성
        // 파일 유효성 검사
        checkInvalidUploadFile(multipartFile);
        validateExtension(multipartFile);
        validateFileSize(multipartFile);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new PetsTableException(FILE_UPLOAD_FAIL.getStatus(), FILE_UPLOAD_FAIL.getMessage(), 400);
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteImage(String directoryPath, String fileName) {
        String fullPath = directoryPath + fileName; // 파일의 전체 경로
        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fullPath));
        } catch (Exception e) {
            throw new PetsTableException(FILE_DELETE_FAIL.getStatus(), FILE_DELETE_FAIL.getMessage(), 400);
        }
    }

    // 이미지 파일 이름을 UUID 기반으로 생성
    public String generateImageFileName(MultipartFile image) {
        String extension = getExtension(Objects.requireNonNull(image.getContentType()));
        if (extension == null) {
            throw new PetsTableException(INVALID_IMAGE_TYPE.getStatus(), INVALID_IMAGE_TYPE.getMessage(), 400);
        }
        return "/" + UUID.randomUUID() + extension;
    }

    // 확장자에 맞는 파일 포맷 가져오기
    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/heic" -> ".heic";
            case "image/heif" -> ".heif";
            default -> ".jpg";
        };
    }

    // 이미지 파일 확장자 유효성 검사
    private void validateExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (!IMAGE_EXTENSIONS.contains(contentType)) {
            throw new PetsTableException(INVALID_IMAGE_TYPE.getStatus(), INVALID_IMAGE_TYPE.getMessage(), 400);
        }
    }

    // 이미지 파일 크기 유효성 검사
    private void validateFileSize(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new PetsTableException(INVALID_IMAGE_SIZE.getStatus(), INVALID_IMAGE_SIZE.getMessage(), 400);
        }
    }

    // 파일이 비어있거나 크기가 0인 경우 유효하지 않음
    private void checkInvalidUploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || multipartFile.getSize() == 0) {
            throw new PetsTableException(FILE_INVALID_EMPTY.getStatus(), FILE_INVALID_EMPTY.getMessage(), 400);
        }
    }
}