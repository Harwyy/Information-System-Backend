package is.is_backend.service;

import is.is_backend.builder.OrganizationBuilder;
import is.is_backend.constraint.OrganizationGeoBusinessConstraint;
import is.is_backend.dto.organizationDto.OrganizationPageRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationResponseDTO;
import is.is_backend.exception.MyException;
import is.is_backend.mapper.OrganizationMapper;
import is.is_backend.models.Organization;
import is.is_backend.models.enums.OrganizationType;
import is.is_backend.repository.OrganizationRepository;
import is.is_backend.specification.OrganizationSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationBuilder organizationBuilder;
    private final NotificationService notificationService;
    private final OrganizationGeoBusinessConstraint organizationGeoBusinessConstraint;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 2))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrganizationResponseDTO createOrganization(OrganizationRequestDTO organizationRequestDTO) {
        Organization organization = organizationBuilder.buildFromRequest(organizationRequestDTO);
        validateConstraints(organization, null);
        Organization savedOrganization = organizationRepository.save(organization);
        notificationService.notifyAllSubscribers();
        return OrganizationMapper.toResponseDTO(savedOrganization);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 2))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO organizationRequestDTO) {
        Organization updatedOrganization = organizationRepository
                .findById(id)
                .orElseThrow(() -> new MyException("Organization not found with id: " + id, HttpStatus.NOT_FOUND));
        Organization organization = organizationBuilder.buildFromRequest(organizationRequestDTO);
        validateConstraints(organization, id);
        updatedOrganization.setName(organization.getName());
        updatedOrganization.setCoordinates(organization.getCoordinates());
        updatedOrganization.setOfficialAddress(organization.getOfficialAddress());
        updatedOrganization.setAnnualTurnover(organization.getAnnualTurnover());
        updatedOrganization.setEmployeesCount(organization.getEmployeesCount());
        updatedOrganization.setRating(organization.getRating());
        updatedOrganization.setFullName(organization.getFullName());
        updatedOrganization.setType(organization.getType());
        updatedOrganization.setPostalAddress(organization.getPostalAddress());
        organizationRepository.save(updatedOrganization);
        notificationService.notifyAllSubscribers();
        return OrganizationMapper.toResponseDTO(updatedOrganization);
    }

    public OrganizationResponseDTO deleteOrganization(Long id) {
        Organization organization = organizationRepository
                .findById(id)
                .orElseThrow(() -> new MyException("Organization not found with id: " + id, HttpStatus.NOT_FOUND));
        organizationRepository.delete(organization);
        notificationService.notifyAllSubscribers();
        return OrganizationMapper.toResponseDTO(organization);
    }

    public Page<Organization> getAllOrganizations(OrganizationPageRequestDTO pageRequest) {
        Specification<Organization> spec = OrganizationSpecification.withFilter(pageRequest);
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort());
        return organizationRepository.findAll(spec, pageable);
    }

    public Organization getOrganization(Long id) {
        return organizationRepository
                .findById(id)
                .orElseThrow(() -> new MyException("Organization not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private void validateConstraints(Organization organization, Long id) {
        if (id != null) {
            validateUniqueFullName(organization.getFullName(), id);
            validateUniqueZipCodeAndType(organization.getPostalAddress().getZipCode(), organization.getType(), id);
            validateAnnualTurnover(organization.getAnnualTurnover(), organization.getEmployeesCount());
            organizationGeoBusinessConstraint.validateGovernmentConstraints(organization);
        } else {
            validateUniqueFullName(organization.getFullName(), null);
            validateUniqueZipCodeAndType(organization.getPostalAddress().getZipCode(), organization.getType(), null);
            validateAnnualTurnover(organization.getAnnualTurnover(), organization.getEmployeesCount());
            organizationGeoBusinessConstraint.validateGovernmentConstraints(organization);
        }
    }

    private void validateUniqueFullName(String fullName, Long excludeId) {
        boolean exists;

        if (excludeId != null) {
            exists = organizationRepository.existsByFullNameAndIdNot(fullName, excludeId);
        } else {
            exists = organizationRepository.existsByFullName(fullName);
        }

        if (exists) {
            throw new MyException("Organization with fullName " + fullName + " already exists", HttpStatus.CONFLICT);
        }
    }

    private void validateUniqueZipCodeAndType(String zipCode, OrganizationType type, Long excludeId) {
        if (zipCode == null) return;

        boolean exists;
        if (excludeId != null) {
            exists = organizationRepository.existsByPostalAddressZipCodeAndTypeAndIdNot(zipCode, type, excludeId);
        } else {
            exists = organizationRepository.existsByPostalAddressZipCodeAndType(zipCode, type);
        }

        if (exists) {
            throw new MyException(
                    "Organization of type '" + type + "' already exists with zip code: " + zipCode,
                    HttpStatus.CONFLICT);
        }
    }

    private void validateAnnualTurnover(Double annualTurnover, Integer employeesCount) {
        if (employeesCount == null) return;

        Double minAnnualTurnover = (double) ((employeesCount + 1) * 12 * 19500);
        if (annualTurnover < minAnnualTurnover) {
            throw new MyException(
                    "Annual turnover is less than the subsistence level, should be greater than " + minAnnualTurnover,
                    HttpStatus.CONFLICT);
        }
    }
}
