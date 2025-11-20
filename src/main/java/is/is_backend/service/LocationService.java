package is.is_backend.service;

import is.is_backend.dto.locationDto.LocationPageRequestDTO;
import is.is_backend.exception.MyException;
import is.is_backend.models.Address;
import is.is_backend.models.Location;
import is.is_backend.repository.AddressRepository;
import is.is_backend.repository.LocationRepository;
import is.is_backend.specification.LocationSpecification;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;

    public Location createLocation(Location location) {
        Location savedLocation = locationRepository.save(location);
        notificationService.notifyAllSubscribers();
        return savedLocation;
    }

    public Location updateLocation(Long id, Location location) {
        Location updatedLocation = locationRepository.findById(id).orElse(null);
        if (updatedLocation == null) {
            throw new MyException("Location not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        updatedLocation.setX(location.getX());
        updatedLocation.setY(location.getY());
        updatedLocation.setZ(location.getZ());
        updatedLocation.setName(location.getName());
        locationRepository.save(updatedLocation);
        notificationService.notifyAllSubscribers();
        return updatedLocation;
    }

    public Location deleteLocation(Long id) {
        Location location = locationRepository.findById(id).orElse(null);
        if (location == null) {
            throw new MyException("Location not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        checkLocationUsage(location);
        locationRepository.delete(location);
        notificationService.notifyAllSubscribers();
        return location;
    }

    private void checkLocationUsage(Location location) {
        List<Address> addresses = addressRepository.findByTownId(location.getId());

        if (!addresses.isEmpty()) {
            throw new MyException(
                    String.format(
                            "Location with id %d is used by %d address(es). Clear addresses first.",
                            location.getId(), addresses.size()),
                    HttpStatus.CONFLICT);
        }
    }

    public Page<Location> getAllLocations(LocationPageRequestDTO pageRequest) {
        Specification<Location> spec = LocationSpecification.withFilter(pageRequest);
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort());
        return locationRepository.findAll(spec, pageable);
    }

    public Location getLocation(Long id) {
        Location location = locationRepository.findById(id).orElse(null);
        if (location == null) {
            throw new MyException("Location not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return location;
    }
}
