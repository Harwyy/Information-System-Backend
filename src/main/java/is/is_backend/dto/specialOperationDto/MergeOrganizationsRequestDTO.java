package is.is_backend.dto.specialOperationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import lombok.Data;

@Data
@Schema(description = "Запрос для объединения организаций")
public class MergeOrganizationsRequestDTO {
    @Schema(description = "ID первой организации", example = "1")
    private Long firstOrganizationId;

    @Schema(description = "ID второй организации", example = "2")
    private Long secondOrganizationId;

    @Schema(description = "Данные организации", implementation = OrganizationRequestDTO.class)
    private OrganizationRequestDTO organization;
}
