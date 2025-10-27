package is.is_backend.specification;

import is.is_backend.dto.organizationDto.OrganizationPageRequestDTO;
import is.is_backend.models.Organization;
import org.springframework.data.jpa.domain.Specification;

public class OrganizationSpecification {

    public static Specification<Organization> withFilter(OrganizationPageRequestDTO filter) {
        return Specification.allOf(SampleSpecification.<Organization>fieldContains(filter.getName(), "name"))
                .and(SampleSpecification.<Organization>fieldContains(filter.getFullName(), "fullName"));
    }
}
