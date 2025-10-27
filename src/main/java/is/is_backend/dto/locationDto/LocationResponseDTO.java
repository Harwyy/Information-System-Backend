package is.is_backend.dto.locationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с данными локации")
public class LocationResponseDTO {
    @Schema(description = "ID локации", example = "1")
    private Long id;

    @Schema(description = "X координата", example = "15.5")
    private float x;

    @Schema(description = "Y координата", example = "25.5")
    private double y;

    @Schema(description = "Z координата", example = "35.5")
    private Float z;

    @Schema(description = "Наименование", example = "Broadway Avenue")
    private String name;
}
