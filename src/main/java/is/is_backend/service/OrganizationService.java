package is.is_backend.service;

import is.is_backend.builder.OrganizationBuilder;
import is.is_backend.dto.organizationDto.OrganizationPageRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationResponseDTO;
import is.is_backend.exception.MyException;
import is.is_backend.mapper.OrganizationMapper;
import is.is_backend.models.Organization;
import is.is_backend.repository.OrganizationRepository;
import is.is_backend.specification.OrganizationSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationBuilder organizationBuilder;
    private final NotificationService notificationService;

    public OrganizationResponseDTO createOrganization(OrganizationRequestDTO organizationRequestDTO) {
        Organization organization = organizationBuilder.buildFromRequest(organizationRequestDTO);
        Organization savedOrganization = organizationRepository.save(organization);
        notificationService.notifyAllSubscribers();
        return OrganizationMapper.toResponseDTO(savedOrganization);
    }

    public OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO organizationRequestDTO) {
        Organization updatedOrganization = organizationRepository
                .findById(id)
                .orElseThrow(() -> new MyException("Organization not found with id: " + id, HttpStatus.NOT_FOUND));
        Organization organization = organizationBuilder.buildFromRequest(organizationRequestDTO);
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
}
