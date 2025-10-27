package is.is_backend.dto.organizationDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import is.is_backend.dto.addressDto.AddressResponseDTO;
import is.is_backend.dto.coordinatesDto.CoordinatesResponseDTO;
import is.is_backend.models.enums.OrganizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с данными организации")
public class OrganizationResponseDTO {
    @Schema(description = "ID адреса", example = "1")
    private Long id;

    @Schema(description = "Краткое наименование", example = "Atom")
    private String name;

    @Schema(description = "Данные координаты", implementation = CoordinatesResponseDTO.class)
    private CoordinatesResponseDTO coordinatesResponse;

    @Schema(description = "Дата создания")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private java.time.ZonedDateTime creationDate;

    @Schema(description = "Данные официального адреса", implementation = AddressResponseDTO.class)
    private AddressResponseDTO officialAddressResponse;

    @Schema(description = "Оборот", example = "11.5")
    private Double annualTurnover;

    @Schema(description = "Количество сотрудников", example = "100")
    private Integer employeesCount;

    @Schema(description = "Рейтинг", example = "4.5")
    private Float rating;

    @Schema(description = "Полное наименование", example = "Atom Corporation")
    private String fullName;

    @Schema(description = "Тип", example = "TRUST")
    private OrganizationType type;

    @Schema(description = "Данные фиктивного адреса", implementation = AddressResponseDTO.class)
    private AddressResponseDTO postalAddressResponse;
}
