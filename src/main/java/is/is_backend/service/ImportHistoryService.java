package is.is_backend.service;

import is.is_backend.dto.importHistoryDto.ImportHistoryResponseDTO;
import is.is_backend.mapper.ImportHistoryMapper;
import is.is_backend.models.ImportHistory;
import is.is_backend.repository.ImportHistoryRepository;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistoryMapper importHistoryMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveImportHistory(ImportHistory importHistory, int status) {
        importHistory.setStatus(status);
        importHistoryRepository.save(importHistory);
    }

    public ImportHistory createImportHistory() {
        ImportHistory importHistory = new ImportHistory();
        importHistory.setCreationDate(ZonedDateTime.now());
        importHistory.setCounter(0);
        return importHistory;
    }

    public Page<ImportHistoryResponseDTO> getImportHistoryWithPagination(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ImportHistory> importHistoryPage = importHistoryRepository.findAll(pageable);
        return importHistoryPage.map(importHistoryMapper::toResponseDTO);
    }
}
