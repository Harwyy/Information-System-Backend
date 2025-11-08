package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.importHistoryDto.ImportHistoryResponseDTO;
import is.is_backend.service.ImportHistoryService;
import is.is_backend.service.ImportService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/import")
@AllArgsConstructor
@Tag(name = "Import API", description = "API для управления импортом данных организаций из JSON файлов")
public class ImportController {

    private ImportService importService;
    private ImportHistoryService importHistoryService;

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
    public ResponseEntity<String> importOrganizations(
            @Parameter(
                            description = "JSON файл с данными организаций",
                            required = true,
                            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                    @RequestParam("file")
                    MultipartFile file) {
        importService.processImport(file);
        return ResponseEntity.ok("Imported successfully");
    }

    @Operation(
            summary = "Получение истории импортов",
            description =
                    "Возвращает пагинированный список всех операций импорта")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "История импортов успешно получена",
                        content = @Content(schema = @Schema(implementation = ImportHistoryResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<Page<ImportHistoryResponseDTO>> getImportHistorySorted(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0") @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "Размер страницы (количество элементов на странице)", example = "10")
                    @RequestParam(defaultValue = "10")
                    int size) {

        Page<ImportHistoryResponseDTO> historyPage = importHistoryService.getImportHistoryWithPagination(page, size);

        return ResponseEntity.ok(historyPage);
    }
}
