package com.backend.cv_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
public class S3FileStorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    private S3Client s3Client;

    @PostConstruct
    private void initializeAmazon() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * Tải file lên S3 vào một thư mục cụ thể và trả về URL công khai.
     * @param file đối tượng MultipartFile từ request.
     * @param folderPath đường dẫn thư mục trên S3 (ví dụ: "cvs/", "avatars/").
     * @return URL của file đã tải lên.
     * @throws IOException khi có lỗi trong quá trình đọc file.
     */
    // THAY ĐỔI CHỮ KÝ: Thêm tham số String folderPath
    public String storeFile(MultipartFile file, String folderPath) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        try {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (Exception e) {

        }

        //THAY ĐỔI LOGIC: Ghép tên thư mục vào key
        // (Ví dụ: "cvs/abc-123-xyz.pdf")
        String uniqueFileName = folderPath + UUID.randomUUID().toString() + fileExtension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 4. Lấy URL cho file mới
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(uniqueFileName)).toExternalForm();

        } catch (S3Exception e) {
            log.error("Lỗi khi upload file lên S3: Key={}, Lỗi={}", uniqueFileName, e.awsErrorDetails().errorMessage(), e);
            throw new IOException(e.getMessage(), e);
        }
    }
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            URL url = new URL(fileUrl);
            String key = url.getPath().substring(1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();


            s3Client.deleteObject(deleteObjectRequest);

        } catch (S3Exception e) {
            System.err.println("Lỗi khi xóa file trên S3: " + e.awsErrorDetails().errorMessage());
        } catch (MalformedURLException e) {
            System.err.println("URL của file S3 không hợp lệ: " + fileUrl);
        }
    }
}
