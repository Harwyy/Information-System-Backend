package is.is_backend.service;

import is.is_backend.dto.importHistoryDto.ImportHistoryResponseDTO;
import is.is_backend.mapper.ImportHistoryMapper;
import is.is_backend.models.ImportHistory;
import is.is_backend.repository.ImportHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistoryMapper importHistoryMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportHistory saveImportHistory(ImportHistory importHistory) {
        return importHistoryRepository.save(importHistory);
    }

    public Page<ImportHistoryResponseDTO> getImportHistoryWithPagination(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ImportHistory> importHistoryPage = importHistoryRepository.findAll(pageable);
        return importHistoryPage.map(importHistoryMapper::toResponseDTO);
    }

    public ImportHistory prepareImportHistory(String fileObjectName) {
        return ImportHistory.builder()
                .counter(0)
                .status(ImportHistory.ImportStatus.PENDING)
                .fileObjectName(fileObjectName)
                .fileUrl(null)
                .build();
    }
}
