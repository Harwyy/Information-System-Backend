package is.is_backend.service;

import is.is_backend.builder.AddressBuilder;
import is.is_backend.dto.addressDto.AddressPageRequestDTO;
import is.is_backend.dto.addressDto.AddressRequestDTO;
import is.is_backend.dto.addressDto.AddressResponseDTO;
import is.is_backend.exception.MyException;
import is.is_backend.mapper.AddressMapper;
import is.is_backend.models.Address;
import is.is_backend.models.Organization;
import is.is_backend.repository.AddressRepository;
import is.is_backend.repository.OrganizationRepository;
import is.is_backend.specification.AddressSpecification;
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
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressBuilder addressBuilder;
    private final OrganizationRepository organizationRepository;
    private final NotificationService notificationService;

    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {
        Address address = addressBuilder.buildFromRequest(addressRequestDTO);
        Address savedAddress = addressRepository.save(address);
        notificationService.notifyAllSubscribers();
        return AddressMapper.toResponseDTO(savedAddress);
    }

    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO addressRequestDTO) {
        Address updatedAddress = addressRepository.findById(id).orElse(null);
        if (updatedAddress == null) {
            throw new MyException("Address not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        Address address = addressBuilder.buildFromRequest(addressRequestDTO);
        updatedAddress.setTown(address.getTown());
        updatedAddress.setZipCode(address.getZipCode());
        addressRepository.save(updatedAddress);
        notificationService.notifyAllSubscribers();
        return AddressMapper.toResponseDTO(updatedAddress);
    }

    public AddressResponseDTO deleteAddress(Long id, Boolean forceDelete, Long redirectToAddressId) {
        validateDeleteParameters(id, forceDelete, redirectToAddressId);
        Address addressToDelete = addressRepository
                .findById(id)
                .orElseThrow(() -> new MyException("Address not found with id: " + id, HttpStatus.NOT_FOUND));
        checkAddressUsage(addressToDelete);
        if (Boolean.TRUE.equals(forceDelete)) {
            performForceDelete(addressToDelete);
        } else {
            Address redirectAddress = addressRepository
                    .findById(redirectToAddressId)
                    .orElseThrow(() ->
                            new MyException("Address not found with id: " + redirectToAddressId, HttpStatus.NOT_FOUND));
            validateRedirectConditions(redirectAddress);
            performRedirectAndDelete(addressToDelete, redirectAddress);
        }
        notificationService.notifyAllSubscribers();
        return AddressMapper.toResponseDTO(addressToDelete);
    }

    public Page<AddressResponseDTO> getAllAddressesWhereLocationNull(Pageable pageable) {
        Page<Address> addresses = addressRepository.findByTownIsNull(pageable);
        return addresses.map(AddressMapper::toResponseDTO);
    }

    public Page<Address> getAllAddresses(AddressPageRequestDTO pageRequest) {
        Specification<Address> spec = AddressSpecification.withFilter(pageRequest);
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort());
        return addressRepository.findAll(spec, pageable);
    }

    public Address getAddress(Long id) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            throw new MyException("Address not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return address;
    }

    private void checkAddressUsage(Address addressToDelete) {
        List<Organization> organizations = organizationRepository.findByPostalAddressIdOrOfficialAddressId(
                addressToDelete.getId(), addressToDelete.getId());
        if (!organizations.isEmpty()) {
            throw new MyException(
                    String.format(
                            "Address with id %d is used by %d organization(s). Clear organizations first.",
                            addressToDelete.getId(), organizations.size()),
                    HttpStatus.CONFLICT);
        }
    }

    private void validateDeleteParameters(Long id, Boolean forceDelete, Long redirectToAddressId) {
        if (Boolean.TRUE.equals(forceDelete) && redirectToAddressId != null) {
            throw new MyException(
                    "Cannot use both forceDelete and redirectToAddressId simultaneously", HttpStatus.BAD_REQUEST);
        }
        if (Boolean.FALSE.equals(forceDelete) && redirectToAddressId == null) {
            throw new MyException(
                    "Either forceDelete=true or redirectToAddressId must be provided", HttpStatus.BAD_REQUEST);
        }

        if (id.equals(redirectToAddressId)) {
            throw new MyException("Cannot redirect to the same address being deleted", HttpStatus.CONFLICT);
        }
    }

    private void validateRedirectConditions(Address redirectAddress) {
        if (redirectAddress != null && redirectAddress.getTown() != null) {
            throw new MyException(
                    "Location for address with id " + redirectAddress.getId() + " is not empty", HttpStatus.CONFLICT);
        }
    }

    private void performForceDelete(Address addressToDelete) {
        addressRepository.delete(addressToDelete);
    }

    private void performRedirectAndDelete(Address addressToDelete, Address redirectAddress) {
        if (redirectAddress != null) {
            redirectAddress.setTown(addressToDelete.getTown());
            addressRepository.save(redirectAddress);
        }
        addressRepository.delete(addressToDelete);
    }
}
