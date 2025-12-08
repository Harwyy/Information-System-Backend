package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.importHistoryDto.ImportHistoryResponseDTO;
import is.is_backend.models.ImportHistory;
import is.is_backend.service.ImportHistoryService;
import is.is_backend.service.ImportService;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@Tag(name = "Import", description = "API для управления импортом данных организаций из JSON файлов")
public class ImportController {

    private final ImportService importService;
    private final ImportHistoryService importHistoryService;

    @Operation(
            summary = "Импорт организаций из файла",
            description = "Загружает JSON файл с организациями и импортирует их в систему.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Файл успешно импортирован",
                        content = @Content(schema = @Schema(implementation = String.class)))
            })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportHistoryResponseDTO> importOrganizations(@RequestParam("file") MultipartFile file) {

        ImportHistory importHistory = importService.processImportWithTransaction(file);

        ImportHistoryResponseDTO response = ImportHistoryResponseDTO.builder()
                .id(importHistory.getId())
                .counter(importHistory.getCounter())
                .status(importHistory.getStatus().toString())
                .fileUrl(importHistory.getFileUrl())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Выгрузка файла из хранилища")
    @GetMapping("/{id}/file")
    public ResponseEntity<InputStreamResource> downloadImportFile(@PathVariable Long id) {

        InputStream inputStream = importService.downloadImportFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "imported-file.json");

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение истории импортов",
            description = "Возвращает пагинированный список всех операций импорта")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "История импортов успешно получена",
                        content = @Content(schema = @Schema(implementation = ImportHistoryResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<Page<ImportHistoryResponseDTO>> getImportHistory(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0") @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "Размер страницы (количество элементов на странице)", example = "10")
                    @RequestParam(defaultValue = "10")
                    int size) {

        Page<ImportHistoryResponseDTO> history = importHistoryService.getImportHistoryWithPagination(page, size);
        return ResponseEntity.ok(history);
    }
}
