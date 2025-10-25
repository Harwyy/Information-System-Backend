package is.is_backend.dto.locationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "Запрос для пагинации локаций")
public class LocationPageRequestDTO {
    @Schema(description = "Номер страницы", example = "0")
    private Integer page = 0;

    @Schema(description = "Размер страницы", example = "20")
    private Integer size = 20;

    @Schema(description = "Поле для сортировки", example = "id")
    private String sortBy = "id";

    @Schema(description = "Направление сортировки", example = "ASC")
    private Sort.Direction direction = Sort.Direction.ASC;

    @Schema(description = "Фильтр по частичному совпадению имени", example = "Test")
    private String nameContains;

    public Sort getSort() {
        return Sort.by(direction, sortBy);
    }
}
