package is.is_backend.builder;

import is.is_backend.dto.addressDto.AddressRequestDTO;
import is.is_backend.dto.locationDto.LocationRequestDTO;
import is.is_backend.exception.MyException;
import is.is_backend.models.Address;
import is.is_backend.models.Location;
import is.is_backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressBuilder {

    private final LocationRepository locationRepository;

    public Address buildFromRequest(AddressRequestDTO addressRequest) {
        validateRequest(addressRequest);
        return Address.builder()
                .zipCode(addressRequest.getZipCode())
                .town(buildLocationFromRequest(addressRequest))
                .build();
    }

    private Location buildLocationFromRequest(AddressRequestDTO addressRequest) {
        if (addressRequest.getLocationId() != null) {
            return getExistingLocationById(addressRequest.getLocationId());
        }

        if (addressRequest.getLocationRequest() != null) {
            return createNewLocationFromRequest(addressRequest.getLocationRequest());
        }
        return null;
    }

    private Location createNewLocationFromRequest(LocationRequestDTO locationRequestDTO) {
        return Location.builder()
                .x(locationRequestDTO.getX())
                .y(locationRequestDTO.getY())
                .z(locationRequestDTO.getZ())
                .name(locationRequestDTO.getName())
                .build();
    }

    private Location getExistingLocationById(Long locationId) {
        return locationRepository
                .findById(locationId)
                .orElseThrow(() -> new MyException("Location not found with id: " + locationId, HttpStatus.NOT_FOUND));
    }

    private void validateRequest(AddressRequestDTO dto) {
        if (dto.getLocationId() != null && dto.getLocationRequest() != null) {
            throw new MyException("Cannot provide both locationId and locationRequest", HttpStatus.BAD_REQUEST);
        }
    }
}
