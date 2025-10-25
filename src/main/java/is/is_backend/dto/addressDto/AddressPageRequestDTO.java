package is.is_backend.dto.addressDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "Запрос для пагинации адресов")
public class AddressPageRequestDTO {
    @Schema(description = "Номер страницы", example = "0")
    private Integer page = 0;

    @Schema(description = "Размер страницы", example = "20")
    private Integer size = 20;

    @Schema(description = "Поле для сортировки", example = "id")
    private String sortBy = "id";

    @Schema(description = "Направление сортировки", example = "ASC")
    private Sort.Direction direction = Sort.Direction.ASC;

    @Schema(description = "Фильтр по частичному совпадению индекс", example = "615616")
    private String zipCode;

    public Sort getSort() {
        return Sort.by(direction, sortBy);
    }
}
