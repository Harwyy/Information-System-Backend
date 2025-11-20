package is.is_backend.dto.coordinatesDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с данными координат")
public class CoordinatesResponseDTO {
    @Schema(description = "ID координаты", example = "1")
    private Long id;

    @Schema(description = "X координата", example = "15")
    private Long x;

    @Schema(description = "Y координата", example = "25.5")
    private Float y;
}
