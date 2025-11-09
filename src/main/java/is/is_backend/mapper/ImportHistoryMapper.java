package is.is_backend.mapper;

import is.is_backend.dto.importHistoryDto.ImportHistoryResponseDTO;
import is.is_backend.models.ImportHistory;
import org.springframework.stereotype.Component;

@Component
public class ImportHistoryMapper {

    public ImportHistoryResponseDTO toResponseDTO(ImportHistory importHistory) {
        ImportHistoryResponseDTO dto = new ImportHistoryResponseDTO();
        dto.setId(importHistory.getId());
        dto.setCreationDate(importHistory.getCreationDate());
        dto.setStatus(importHistory.getStatus());
        dto.setCounter(importHistory.getCounter());
        return dto;
    }
}
