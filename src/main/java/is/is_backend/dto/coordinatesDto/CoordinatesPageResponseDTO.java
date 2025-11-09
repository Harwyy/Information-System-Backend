package is.is_backend.dto.coordinatesDto;

import is.is_backend.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

public class CoordinatesPageResponseDTO extends PageResponseDTO<CoordinatesResponseDTO> {
    public static CoordinatesPageResponseDTO fromPage(Page<CoordinatesResponseDTO> page) {
        CoordinatesPageResponseDTO response = new CoordinatesPageResponseDTO();
        response.setPageData(page);
        return response;
    }
}
