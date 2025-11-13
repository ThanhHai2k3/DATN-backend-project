package com.backend.message_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
public class S3FileStorageService {

    // Inject các giá trị từ file application.yml
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    private S3Client s3Client;

    // @PostConstruct đảm bảo phương thức này được gọi sau khi các dependency được inject.
    // Chúng ta khởi tạo S3Client ở đây.
    @PostConstruct
    private void initializeAmazon() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * Tải file lên S3 và trả về URL công khai của file đó.
     * @param file đối tượng MultipartFile từ request.
     * @return URL của file đã tải lên.
     * @throws IOException khi có lỗi trong quá trình đọc file.
     */
    public String storeFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        try {
            // Lấy đuôi file (ví dụ: .png, .jpg)
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (Exception e) {
            // Bỏ qua nếu file không có đuôi
        }

        // Tạo một tên file duy nhất bằng UUID để tránh bị ghi đè file trên S3
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Tạo request để tải file lên S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)       // Tên bucket
                    .key(uniqueFileName)      // Tên file trên S3
                    .contentType(file.getContentType()) // Loại nội dung (ví dụ: image/jpeg)
                    .build();

            // Thực hiện tải file lên
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Lấy và trả về URL công khai của file vừa tải lên
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(uniqueFileName)).toExternalForm();

        } catch (S3Exception e) {
            // Bắt lỗi cụ thể từ AWS S3 và ghi log
            System.err.println("Lỗi khi upload file lên S3: " + e.awsErrorDetails().errorMessage());
            throw new IOException(e.getMessage(), e);
        }
    }
}