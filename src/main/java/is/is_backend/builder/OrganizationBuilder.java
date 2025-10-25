package is.is_backend.builder;

import is.is_backend.dto.coordinatesDto.CoordinatesRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.exception.MyException;
import is.is_backend.models.Address;
import is.is_backend.models.Coordinates;
import is.is_backend.models.Organization;
import is.is_backend.repository.AddressRepository;
import is.is_backend.repository.CoordinatesRepository;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrganizationBuilder {

    private final CoordinatesRepository coordinatesRepository;
    private final AddressRepository addressRepository;
    private final AddressBuilder addressBuilder;

    public Organization buildFromRequest(OrganizationRequestDTO organizationRequest) {
        validateRequest(organizationRequest);

        return Organization.builder()
                .name(organizationRequest.getName())
                .coordinates(buildCoordinates(organizationRequest))
                .creationDate(ZonedDateTime.now())
                .officialAddress(buildOfficialAddressFromRequest(organizationRequest))
                .annualTurnover(organizationRequest.getAnnualTurnover())
                .employeesCount(organizationRequest.getEmployeesCount())
                .rating(organizationRequest.getRating())
                .fullName(organizationRequest.getFullName())
                .type(organizationRequest.getType())
                .postalAddress(buildPostalAddressFromRequest(organizationRequest))
                .build();
    }

    private Address buildPostalAddressFromRequest(OrganizationRequestDTO organizationRequest) {
        if (organizationRequest.getPostalAddressId() != null) {
            return getExistingAddressById(organizationRequest.getPostalAddressId());
        }
        return addressBuilder.buildFromRequest(organizationRequest.getPostalAddressRequest());
    }

    private Address buildOfficialAddressFromRequest(OrganizationRequestDTO organizationRequest) {
        if (organizationRequest.getOfficialAddressId() != null) {
            return getExistingAddressById(organizationRequest.getOfficialAddressId());
        }
        return addressBuilder.buildFromRequest(organizationRequest.getOfficialAddressRequest());
    }

    private Address getExistingAddressById(Long addressId) {
        return addressRepository
                .findById(addressId)
                .orElseThrow(() -> new MyException("Address not found with id: " + addressId, HttpStatus.NOT_FOUND));
    }

    private Coordinates buildCoordinates(OrganizationRequestDTO organizationRequest) {
        if (organizationRequest.getCoordinatesId() != null) {
            return getExistingCoordinatesById(organizationRequest.getCoordinatesId());
        }
        return createNewCoordinatesFromRequest(organizationRequest.getCoordinatesRequest());
    }

    private Coordinates createNewCoordinatesFromRequest(CoordinatesRequestDTO coordinatesRequest) {
        return Coordinates.builder()
                .x(coordinatesRequest.getX())
                .y(coordinatesRequest.getY())
                .build();
    }

    private Coordinates getExistingCoordinatesById(Long coordinatesId) {
        return coordinatesRepository
                .findById(coordinatesId)
                .orElseThrow(
                        () -> new MyException("Coordinates not found with id: " + coordinatesId, HttpStatus.NOT_FOUND));
    }

    private void validateRequest(OrganizationRequestDTO organizationRequest) {
        if (organizationRequest.getName() == null
                || organizationRequest.getName().trim().isEmpty()) {
            throw new MyException("Organization name cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (organizationRequest.getFullName() == null
                || organizationRequest.getFullName().trim().isEmpty()) {
            throw new MyException("Full name cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        if (organizationRequest.getCoordinatesRequest() == null && organizationRequest.getCoordinatesId() == null) {
            throw new MyException("Coordinates cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (organizationRequest.getOfficialAddressRequest() == null
                && organizationRequest.getOfficialAddressId() == null) {
            throw new MyException("Official address cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (organizationRequest.getPostalAddressRequest() == null && organizationRequest.getPostalAddressId() == null) {
            throw new MyException("Postal address cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (organizationRequest.getType() == null) {
            throw new MyException("Organization type cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (organizationRequest.getAnnualTurnover() == null || organizationRequest.getAnnualTurnover() <= 0) {
            throw new MyException("Annual turnover must be greater than 0", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (organizationRequest.getEmployeesCount() != null && organizationRequest.getEmployeesCount() <= 0) {
            throw new MyException(
                    "Employees count must be greater than 0 if provided", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (organizationRequest.getRating() != null && organizationRequest.getRating() <= 0) {
            throw new MyException("Rating must be greater than 0 if provided", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (organizationRequest.getPostalAddressRequest() != null && organizationRequest.getPostalAddressId() != null) {
            throw new MyException("Cannot provide both postalAddressRequest and postalAddressId", HttpStatus.CONFLICT);
        }
        if (organizationRequest.getOfficialAddressId() != null
                && organizationRequest.getOfficialAddressRequest() != null) {
            throw new MyException(
                    "Cannot provide both officialAddressId and officialAddressRequest", HttpStatus.CONFLICT);
        }
        if (organizationRequest.getCoordinatesRequest() != null && organizationRequest.getCoordinatesId() != null) {
            throw new MyException("Cannot provide both coordinatesId and coordinatesRequest", HttpStatus.CONFLICT);
        }
    }
}
