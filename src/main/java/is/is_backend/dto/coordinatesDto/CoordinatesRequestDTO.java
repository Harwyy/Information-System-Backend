package is.is_backend.dto.coordinatesDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос для создания/обновления координат")
public class CoordinatesRequestDTO {
    @Schema(description = "X координата", example = "15")
    private long x;

    @Schema(description = "Y координата", example = "25.5")
    private float y;
}
