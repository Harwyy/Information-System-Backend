package is.is_backend.dto.addressDto;

import io.swagger.v3.oas.annotations.media.Schema;
import is.is_backend.dto.locationDto.LocationResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с данными адреса")
public class AddressResponseDTO {
    @Schema(description = "ID адреса", example = "1")
    private Long id;

    @Schema(description = "Индекс", example = "615615")
    private String zipCode;

    @Schema(description = "Данные локации", implementation = LocationResponseDTO.class)
    private LocationResponseDTO locationDTO;
}
