package is.is_backend.dto.locationDto;

import is.is_backend.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

public class LocationPageResponseDTO extends PageResponseDTO<LocationResponseDTO> {
    public static LocationPageResponseDTO fromPage(Page<LocationResponseDTO> page) {
        LocationPageResponseDTO response = new LocationPageResponseDTO();
        response.setPageData(page);
        return response;
    }
}
