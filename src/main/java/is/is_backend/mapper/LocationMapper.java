package is.is_backend.mapper;

import is.is_backend.dto.locationDto.LocationRequestDTO;
import is.is_backend.dto.locationDto.LocationResponseDTO;
import is.is_backend.models.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public static Location toEntity(LocationRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Location location = new Location();
        location.setX(dto.getX());
        location.setY(dto.getY());
        location.setZ(dto.getZ());
        location.setName(dto.getName());
        return location;
    }

    public static LocationResponseDTO toResponseDTO(Location location) {
        if (location == null) {
            return null;
        }

        return LocationResponseDTO.builder()
                .id(location.getId())
                .x(location.getX())
                .y(location.getY())
                .z(location.getZ())
                .name(location.getName())
                .build();
    }
}
