package is.is_backend.dto.specialOperationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ для группировки организаций по полному наименованию")
public class OrganizationCountByFullNameResponseDTO {
    @Schema(description = "Полное наименование", example = "Atom Corporation")
    private String fullName;

    @Schema(description = "Количество", example = "3")
    private Long count;
}
