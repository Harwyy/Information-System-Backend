package is.is_backend.dto.locationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос для создания/обновления локации")
public class LocationRequestDTO {
    @Schema(description = "X координата", example = "15.5")
    private float x;

    @Schema(description = "Y координата", example = "25.5")
    private double y;

    @Schema(description = "Z координата", example = "35.5", nullable = true)
    @NotNull(message = "Z coordinate is required")
    private Float z;

    @Schema(description = "Наименование", example = "Broadway Avenue")
    private String name;
}
