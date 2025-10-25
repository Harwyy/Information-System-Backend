package is.is_backend.mapper;

import is.is_backend.dto.organizationDto.OrganizationResponseDTO;
import is.is_backend.models.Organization;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public static OrganizationResponseDTO toResponseDTO(Organization organization) {
        if (organization == null) {
            return null;
        }

        return OrganizationResponseDTO.builder()
                .id(organization.getId())
                .name(organization.getName())
                .coordinatesResponse(CoordinatesMapper.toResponseDTO(organization.getCoordinates()))
                .creationDate(organization.getCreationDate())
                .officialAddressResponse(AddressMapper.toResponseDTO(organization.getOfficialAddress()))
                .annualTurnover(organization.getAnnualTurnover())
                .employeesCount(organization.getEmployeesCount())
                .rating(organization.getRating())
                .fullName(organization.getFullName())
                .type(organization.getType())
                .postalAddressResponse(AddressMapper.toResponseDTO(organization.getPostalAddress()))
                .build();
    }
}
