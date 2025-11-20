package is.is_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.exception.MyException;
import is.is_backend.models.ImportHistory;
import is.is_backend.repository.ImportHistoryRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ImportService {

    private ImportHistoryRepository importHistoryRepository;
    private ObjectMapper objectMapper;
    private OrganizationService organizationService;
    private ImportHistoryService importHistoryService;

    private static final int SUCCESS_STATUS = 0;
    private static final int ERROR_STATUS = 1;
    private static final int MAX_SIZE = 100;

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ImportHistory processImport(MultipartFile file) {
        if (file.isEmpty()) {
            throw new MyException("File cant be empty.", HttpStatus.BAD_REQUEST);
        }

        if (!isJsonFile(file)) {
            throw new MyException("Invalid file format.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        ImportHistory importHistory = importHistoryService.createImportHistory();

        try {
            List<OrganizationRequestDTO> organizations = parseJSON(file);

            if (organizations.size() >= MAX_SIZE) {
                importHistoryService.saveImportHistory(importHistory, ERROR_STATUS);
                throw new MyException(
                        "Maximum import limit reached. Make it smaller than " + MAX_SIZE + ".", HttpStatus.BAD_REQUEST);
            }

            for (OrganizationRequestDTO organization : organizations) {
                organizationService.createOrganization(organization);
                importHistory.setCounter(importHistory.getCounter() + 1);
            }

            importHistoryService.saveImportHistory(importHistory, SUCCESS_STATUS);
            return importHistory;
        } catch (RuntimeException e) {
            importHistoryService.saveImportHistory(importHistory, ERROR_STATUS);
            throw new MyException(
                    "Import failed (error in object " + (importHistory.getCounter() + 1) + "). " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private List<OrganizationRequestDTO> parseJSON(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            List<OrganizationRequestDTO> result = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, OrganizationRequestDTO.class));
            return result;
        } catch (IOException e) {
            throw new MyException("Failed to parse JSON file", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isJsonFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        return "application/json".equals(contentType)
                || (filename != null && filename.toLowerCase().endsWith(".json"));
    }
}
