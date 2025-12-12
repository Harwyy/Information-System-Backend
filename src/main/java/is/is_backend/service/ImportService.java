package is.is_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.exception.MyException;
import is.is_backend.models.ImportHistory;
import is.is_backend.repository.ImportHistoryRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ObjectMapper objectMapper;
    private final OrganizationService organizationService;
    private final MinioService minioService;
    private final ImportHistoryService importHistoryService;
    private final ImportHistoryRepository importHistoryRepository;

    private static final int MAX_SIZE = 100;

    public ImportHistory processImportWithTransaction(MultipartFile file) {
        validateFile(file);

        String transactionId = UUID.randomUUID().toString();

        ImportHistory importHistory;
        String savedFileObjectName = null;

        try {
            savedFileObjectName = prepareFileInMinio(file, transactionId);

            importHistory = importHistoryService.prepareImportHistory(savedFileObjectName);

            List<OrganizationRequestDTO> organizations = parseJSON(file);
            validateOrganizationsSize(organizations);

            commitTransaction(importHistory, organizations, savedFileObjectName);

            importHistoryService.saveImportHistory(importHistory);

            return importHistory;

        } catch (Exception e) {
            rollbackTransaction(savedFileObjectName);
            importHistory = importHistoryService.prepareImportHistory("savedFileObjectName");
            importHistory.setStatus(ImportHistory.ImportStatus.ROLLED_BACK);
            importHistoryService.saveImportHistory(importHistory);
            throw new MyException("Import failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class)
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void commitTransaction(
            ImportHistory importHistory, List<OrganizationRequestDTO> organizations, String fileObjectName) {
        try {
            for (OrganizationRequestDTO organization : organizations) {
                organizationService.createOrganization(organization);
                importHistory.setCounter(importHistory.getCounter() + 1);
            }
            importHistory.setStatus(ImportHistory.ImportStatus.COMMITTED);
            String fileUrl = minioService.getFileUrl(fileObjectName);
            importHistory.setFileUrl(fileUrl);
            minioService.updateFileStatus(fileObjectName, "COMMITTED");
        } catch (Exception e) {
            throw new MyException("Import commit failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public InputStream downloadImportFile(Long importId) {
        ImportHistory importHistory = importHistoryRepository
                .findById(importId)
                .orElseThrow(() -> new MyException("Import history not found", HttpStatus.NOT_FOUND));

        if (importHistory.getStatus() != ImportHistory.ImportStatus.COMMITTED) {
            throw new MyException("File is not available for download", HttpStatus.BAD_REQUEST);
        }

        if (importHistory.getFileObjectName() == null) {
            throw new MyException("File not found in storage", HttpStatus.NOT_FOUND);
        }

        return minioService.getFile(importHistory.getFileObjectName());
    }

    public void rollbackTransaction(String fileObjectName) {
        if (fileObjectName != null) {
            minioService.deleteFile(fileObjectName);
        }
    }

    private String prepareFileInMinio(MultipartFile file, String transactionId) {
        try {
            return minioService.saveFile(file, transactionId);
        } catch (Exception e) {
            throw new MyException(
                    "Failed to save file to storage: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new MyException("File can't be empty.", HttpStatus.BAD_REQUEST);
        }

        if (!isJsonFile(file)) {
            throw new MyException("Invalid file format. Expected JSON.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    private void validateOrganizationsSize(List<OrganizationRequestDTO> organizations) {
        if (organizations.size() >= MAX_SIZE) {
            throw new MyException(
                    "Maximum import limit reached. Must be smaller than " + MAX_SIZE + ".", HttpStatus.BAD_REQUEST);
        }
    }

    private List<OrganizationRequestDTO> parseJSON(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, OrganizationRequestDTO.class));
        } catch (IOException e) {
            throw new MyException("Failed to parse JSON file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isJsonFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        return "application/json".equals(contentType)
                || (filename != null && filename.toLowerCase().endsWith(".json"));
    }
}
