package is.is_backend.dto.organizationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import is.is_backend.dto.addressDto.AddressRequestDTO;
import is.is_backend.dto.coordinatesDto.CoordinatesRequestDTO;
import is.is_backend.models.enums.OrganizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос для создания/обновления организации")
public class OrganizationRequestDTO {
    @Schema(description = "Краткое наименование", example = "Atom")
    private String name;

    @Schema(description = "Данные координаты", implementation = CoordinatesRequestDTO.class)
    private CoordinatesRequestDTO coordinatesRequest;

    @Schema(description = "ID существующей координаты (альтернатива созданию новой)", example = "10")
    private Long coordinatesId;

    private java.time.ZonedDateTime creationDate;

    @Schema(description = "Данные официального адреса", implementation = AddressRequestDTO.class)
    private AddressRequestDTO officialAddressRequest;

    @Schema(description = "ID существующей официального адреса (альтернатива созданию новой)", example = "10")
    private Long officialAddressId;

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

    @Schema(description = "Данные фиктивного адреса", implementation = AddressRequestDTO.class)
    private AddressRequestDTO postalAddressRequest;

    @Schema(description = "ID существующей фиктивного адреса (альтернатива созданию новой)", example = "10")
    private Long postalAddressId;
}
