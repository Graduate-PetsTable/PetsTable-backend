package com.example.petstable.global.support;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.example.petstable.domain.board.dto.response.PreSignedUrlResponse;
import com.example.petstable.global.config.AmazonConfig;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.example.petstable.global.support.UploadMessage.*;

@Component
@RequiredArgsConstructor
public class AwsS3Uploader {

    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp", "image/heic", "image/heif");
    private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L; // 최대 5MB

    private final AmazonConfig amazonConfig;
    private final AmazonS3 amazonS3Client;

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
            amazonS3Client.putObject(new PutObjectRequest(amazonConfig.getBucket(), fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new PetsTableException(FILE_UPLOAD_FAIL.getStatus(), FILE_UPLOAD_FAIL.getMessage(), 400);
        }
        return amazonConfig.getCloudfrontUri() + amazonS3Client.getUrl(amazonConfig.getBucket(), fileName).toString();
    }

    public void deleteImage(String directoryPath, String fileName) {
        String fullPath = directoryPath + fileName; // 파일의 전체 경로
        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), fullPath));
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

    public PreSignedUrlResponse getPreSignedUrl(String prefix, String originalFilename) {
        String fileName = createPath(prefix, originalFilename);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(amazonConfig.getBucket(), fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return PreSignedUrlResponse.toPreSignedUrlResponse(url);
    }

    // presigned url 유효 기간 설정
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60 * 24 * 7;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    // 파일의 전체 경로를 생성
    private String createPath(String directoryPath, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s-%s", directoryPath, fileId, fileName);
    }
}