package is.is_backend.mapper;

import is.is_backend.dto.importHistoryDto.ImportHistoryResponseDTO;
import is.is_backend.models.ImportHistory;
import org.springframework.stereotype.Component;

@Component
public class ImportHistoryMapper {

    public ImportHistoryResponseDTO toResponseDTO(ImportHistory importHistory) {
        if (importHistory == null) {
            return null;
        }

        return ImportHistoryResponseDTO.builder()
                .id(importHistory.getId())
                .creationDate(importHistory.getCreationDate())
                .status(
                        importHistory.getStatus() != null
                                ? importHistory.getStatus().name()
                                : null)
                .counter(importHistory.getCounter())
                .fileObjectName(importHistory.getFileObjectName())
                .fileUrl(importHistory.getFileUrl())
                .build();
    }
}
