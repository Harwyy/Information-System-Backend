package is.is_backend.dto.organizationDto;

import is.is_backend.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

public class OrganizationPageResponseDTO extends PageResponseDTO<OrganizationResponseDTO> {
    public static OrganizationPageResponseDTO fromPage(Page<OrganizationResponseDTO> page) {
        OrganizationPageResponseDTO response = new OrganizationPageResponseDTO();
        response.setPageData(page);
        return response;
    }
}
