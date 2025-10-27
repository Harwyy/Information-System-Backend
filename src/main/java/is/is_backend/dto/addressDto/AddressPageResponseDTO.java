package is.is_backend.dto.addressDto;

import is.is_backend.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

public class AddressPageResponseDTO extends PageResponseDTO<AddressResponseDTO> {
    public static AddressPageResponseDTO fromPage(Page<AddressResponseDTO> page) {
        AddressPageResponseDTO response = new AddressPageResponseDTO();
        response.setPageData(page);
        return response;
    }
}
