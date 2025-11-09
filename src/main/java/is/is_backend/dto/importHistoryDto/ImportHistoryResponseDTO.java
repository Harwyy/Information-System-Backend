package is.is_backend.dto.importHistoryDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
@Schema(description = "DTO для истории импорта")
public class ImportHistoryResponseDTO {
    @Schema(description = "ID истории импорта", example = "1")
    private Long id;

    @Schema(description = "Дата создания")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private ZonedDateTime creationDate;

    @Schema(description = "Статус импорта", example = "0")
    private Integer status;

    @Schema(description = "Количество обработанных записей", example = "10")
    private Integer counter;
}
