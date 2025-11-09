package is.is_backend.specification;

import is.is_backend.dto.locationDto.LocationPageRequestDTO;
import is.is_backend.models.Location;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LocationSpecification {

    public static Specification<Location> withFilter(LocationPageRequestDTO filter) {
        return Specification.allOf(SampleSpecification.<Location>fieldContains(filter.getNameContains(), "name"));
    }
}
