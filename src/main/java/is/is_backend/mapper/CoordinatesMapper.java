package is.is_backend.mapper;

import is.is_backend.dto.coordinatesDto.CoordinatesRequestDTO;
import is.is_backend.dto.coordinatesDto.CoordinatesResponseDTO;
import is.is_backend.models.Coordinates;
import org.springframework.stereotype.Component;

@Component
public class CoordinatesMapper {

    public static Coordinates toEntity(CoordinatesRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());
        return coordinates;
    }

    public static CoordinatesResponseDTO toResponseDTO(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }

        return CoordinatesResponseDTO.builder()
                .id(coordinates.getId())
                .x(coordinates.getX())
                .y(coordinates.getY())
                .build();
    }
}
