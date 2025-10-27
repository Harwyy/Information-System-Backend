package is.is_backend.service;

import is.is_backend.dto.coordinatesDto.CoordinatesPageRequestDTO;
import is.is_backend.exception.MyException;
import is.is_backend.models.Coordinates;
import is.is_backend.models.Organization;
import is.is_backend.repository.CoordinatesRepository;
import is.is_backend.repository.OrganizationRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoordinatesService {

    private CoordinatesRepository coordinatesRepository;
    private OrganizationRepository organizationRepository;
    private final NotificationService notificationService;

    public Coordinates createCoordinate(Coordinates coordinates) {
        Coordinates savedCoordinates = coordinatesRepository.save(coordinates);
        notificationService.notifyAllSubscribers();
        return savedCoordinates;
    }

    public Coordinates updateCoordinate(Long id, Coordinates coordinates) {
        Coordinates updatedCoordinates = coordinatesRepository.findById(id).orElse(null);
        if (updatedCoordinates == null) {
            throw new MyException("Coordinates not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        updatedCoordinates.setX(coordinates.getX());
        updatedCoordinates.setY(coordinates.getY());
        coordinatesRepository.save(updatedCoordinates);
        notificationService.notifyAllSubscribers();
        return updatedCoordinates;
    }

    public Coordinates deleteCoordinate(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id).orElse(null);
        if (coordinates == null) {
            throw new MyException("Coordinates not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        checkCoordinatesUsage(coordinates);
        coordinatesRepository.delete(coordinates);
        notificationService.notifyAllSubscribers();
        return coordinates;
    }

    private void checkCoordinatesUsage(Coordinates coordinates) {
        List<Organization> organizations = organizationRepository.findByCoordinatesId(coordinates.getId());
        if (!organizations.isEmpty()) {
            throw new MyException(
                    String.format(
                            "Coordinate with id %d is used by %d organization(s). Clear organizations first.",
                            coordinates.getId(), organizations.size()),
                    HttpStatus.CONFLICT);
        }
    }

    public Page<Coordinates> getAllCoordinates(CoordinatesPageRequestDTO pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort());
        return coordinatesRepository.findAll(pageable);
    }

    public Coordinates getCoordinates(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id).orElse(null);
        if (coordinates == null) {
            throw new MyException("Coordinates not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return coordinates;
    }
}
