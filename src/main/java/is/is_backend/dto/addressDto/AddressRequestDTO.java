package is.is_backend.dto.addressDto;

import io.swagger.v3.oas.annotations.media.Schema;
import is.is_backend.dto.locationDto.LocationRequestDTO;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@Schema(description = "Запрос для создания/обновления адреса")
public class AddressRequestDTO {
    @Schema(description = "Индекс", example = "615615")
    private String zipCode;

    @Valid
    @Schema(description = "Данные локации", implementation = LocationRequestDTO.class)
    private LocationRequestDTO locationRequest;

    @Schema(description = "ID существующей локации (альтернатива созданию новой)", example = "10")
    private Long locationId;
}
