package is.is_backend.specification;

import is.is_backend.dto.addressDto.AddressPageRequestDTO;
import is.is_backend.models.Address;
import org.springframework.data.jpa.domain.Specification;

public class AddressSpecification {

    public static Specification<Address> withFilter(AddressPageRequestDTO filter) {
        return Specification.allOf(SampleSpecification.<Address>fieldContains(filter.getZipCode(), "zipCode"));
    }
}
