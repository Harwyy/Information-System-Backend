package is.is_backend.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import is.is_backend.config.MinioConfig;
import is.is_backend.exception.MyException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    private static final Duration PENDING_MAX_AGE = Duration.ofDays(1);

    public int cleanupOldPendingFiles() {
        int deletedCount = 0;
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minus(PENDING_MAX_AGE);

            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .prefix("imports/")
                    .recursive(true)
                    .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(objectName)
                        .build());
                Map<String, String> metadata = stat.userMetadata();
                String status = metadata.get("status");
                String uploadTimeStr = metadata.get("upload-time");
                if ("PENDING".equals(status) && uploadTimeStr != null) {
                    LocalDateTime uploadTime = LocalDateTime.parse(uploadTimeStr);
                    if (uploadTime.isBefore(cutoffTime)) {
                        deleteFile(objectName);
                        deletedCount++;
                    }
                }
            }

        } catch (Exception e) {
            throw new MyException("Failed to cleanup old pending files", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return deletedCount;
    }

    public String saveFile(MultipartFile file, String importId) {
        try {
            ensureBucketExists();

            String objectName = generateObjectName(file.getOriginalFilename(), importId);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("import-id", importId);
            metadata.put("original-filename", file.getOriginalFilename());
            metadata.put("upload-time", LocalDateTime.now().toString());
            metadata.put("status", "PENDING");

            minioClient.putObject(PutObjectArgs.builder().bucket(minioConfig.getBucket()).object(objectName).stream(
                            file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .userMetadata(metadata)
                    .build());

            return objectName;

        } catch (Exception e) {
            throw new MyException("Failed to save file to storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public InputStream getFile(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new MyException("File not found in storage", HttpStatus.NOT_FOUND);
        }
    }

    public String getFileUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS)
                    .build());
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new MyException("Failed to delete file from storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateFileStatus(String objectName, String status) {
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .build());

            Map<String, String> metadata = stat.userMetadata();
            metadata.put("status", status);

            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .source(CopySource.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .build())
                    .userMetadata(metadata)
                    .metadataDirective(Directive.REPLACE)
                    .build());

        } catch (Exception e) {
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build());

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());

            String policy =
                    """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {"AWS": ["*"]},
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """
                            .formatted(minioConfig.getBucket());

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .config(policy)
                    .build());
        }
    }

    private String generateObjectName(String originalFilename, String importId) {
        String timestamp = String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return String.format("imports/%s/%s%s", importId, timestamp, extension);
    }
}
