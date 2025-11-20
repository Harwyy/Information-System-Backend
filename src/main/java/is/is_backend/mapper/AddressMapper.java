package is.is_backend.mapper;

import is.is_backend.dto.addressDto.AddressResponseDTO;
import is.is_backend.models.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public static AddressResponseDTO toResponseDTO(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDTO.builder()
                .id(address.getId())
                .zipCode(address.getZipCode())
                .locationDTO(LocationMapper.toResponseDTO(address.getTown()))
                .build();
    }
}
