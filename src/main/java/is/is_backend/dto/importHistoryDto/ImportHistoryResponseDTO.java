package is.is_backend.dto.importHistoryDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "DTO для истории импорта")
@Builder
public class ImportHistoryResponseDTO {
    @Schema(description = "ID истории импорта", example = "1")
    private Long id;

    @Schema(description = "Дата создания")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private ZonedDateTime creationDate;

    @Schema(
            description = "Статус импорта",
            example = "PENDING",
            allowableValues = {"PENDING", "COMMITTED", "ROLLED_BACK"})
    private String status;

    @Schema(description = "Количество обработанных записей", example = "10")
    private Integer counter;

    @Schema(description = "Имя файла в хранилище")
    private String fileObjectName;

    @Schema(description = "URL для скачивания файла")
    private String fileUrl;
}
