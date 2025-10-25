package is.is_backend.service;

import is.is_backend.builder.OrganizationBuilder;
import is.is_backend.dto.organizationDto.OrganizationRequestDTO;
import is.is_backend.dto.organizationDto.OrganizationResponseDTO;
import is.is_backend.dto.specialOperationDto.MergeOrganizationsRequestDTO;
import is.is_backend.dto.specialOperationDto.OrganizationCountByFullNameResponseDTO;
import is.is_backend.exception.MyException;
import is.is_backend.mapper.OrganizationMapper;
import is.is_backend.models.Organization;
import is.is_backend.repository.OrganizationRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SpecialOperationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationBuilder organizationBuilder;
    private final NotificationService notificationService;

    public OrganizationResponseDTO getOrganizationWithMaxOfficialAddress() {
        List<Organization> organizationList = organizationRepository.findAll();
        Organization organizationWithMaxAddress = findOrganizationWithMaxOfficialAddress(organizationList);
        return OrganizationMapper.toResponseDTO(organizationWithMaxAddress);
    }

    public List<OrganizationCountByFullNameResponseDTO> getOrganizationCountByFullName() {
        List<Organization> organizations = organizationRepository.findAll();
        Map<String, Long> countByFullName = countOrganizationsByFullName(organizations);
        return convertToDtoList(countByFullName);
    }

    public List<OrganizationResponseDTO> getOrganizationWhereFullNameContain(String fullName) {
        validateSearchSubstring(fullName);
        List<Organization> organizations = organizationRepository.findAll();
        return filterOrganizationsByFullNameSubstring(organizations, fullName);
    }

    public OrganizationResponseDTO updateCountOfEmployee(Long id) {
        Organization updatedOrganization = organizationRepository
                .findById(id)
                .orElseThrow(() -> new MyException("Organization not found with id: " + id, HttpStatus.NOT_FOUND));
        updatedOrganization.setEmployeesCount(
                updatedOrganization.getEmployeesCount() == null ? 0 : updatedOrganization.getEmployeesCount() + 1);
        organizationRepository.save(updatedOrganization);
        notificationService.notifyAllSubscribers();
        return OrganizationMapper.toResponseDTO(updatedOrganization);
    }

    public OrganizationResponseDTO joinOrganization(MergeOrganizationsRequestDTO joinOrganizationRequestDTO) {
        validateJoinRequest(joinOrganizationRequestDTO);
        Organization firstOrganization = findOrganizationById(joinOrganizationRequestDTO.getFirstOrganizationId());
        Organization secondOrganization = findOrganizationById(joinOrganizationRequestDTO.getSecondOrganizationId());
        applyMergeCalculations(joinOrganizationRequestDTO.getOrganization(), firstOrganization, secondOrganization);
        Organization mergedOrganization =
                organizationBuilder.buildFromRequest(joinOrganizationRequestDTO.getOrganization());
        Organization savedOrganization = organizationRepository.save(mergedOrganization);
        notificationService.notifyAllSubscribers();
        return OrganizationMapper.toResponseDTO(savedOrganization);
    }

    private void applyMergeCalculations(
            OrganizationRequestDTO organization, Organization firstOrganization, Organization secondOrganization) {
        organization.setAnnualTurnover(
                calculateMergedAnnualTurnover(organization, firstOrganization, secondOrganization));
        organization.setRating(calculateAverageRating(organization, firstOrganization, secondOrganization));
        organization.setEmployeesCount(calculateTotalEmployees(firstOrganization, secondOrganization));
    }

    private Float calculateAverageRating(
            OrganizationRequestDTO organizationRequestDTO,
            Organization firstOrganization,
            Organization secondOrganization) {
        if (organizationRequestDTO.getRating() != null) {
            return organizationRequestDTO.getRating();
        }
        float firstRating = firstOrganization.getRating() != null ? firstOrganization.getRating() : 0.0f;
        float secondRating = secondOrganization.getRating() != null ? secondOrganization.getRating() : 0.0f;
        return (firstRating + secondRating)
                / ((firstOrganization.getRating() != null ? 1 : 0) + (secondOrganization.getRating() != null ? 1 : 0));
    }

    private Integer calculateTotalEmployees(Organization firstOrganization, Organization secondOrganization) {
        Integer firstEmployees =
                firstOrganization.getEmployeesCount() != null ? firstOrganization.getEmployeesCount() : 0;
        Integer secondEmployees =
                secondOrganization.getEmployeesCount() != null ? secondOrganization.getEmployeesCount() : 0;
        return firstEmployees + secondEmployees;
    }

    private Double calculateMergedAnnualTurnover(
            OrganizationRequestDTO organizationRequestDTO,
            Organization firstOrganization,
            Organization secondOrganization) {
        if (organizationRequestDTO.getAnnualTurnover() != null) {
            return organizationRequestDTO.getAnnualTurnover();
        }
        double firstTurnover =
                firstOrganization.getAnnualTurnover() != null ? firstOrganization.getAnnualTurnover() : 0.0;
        double secondTurnover =
                secondOrganization.getAnnualTurnover() != null ? secondOrganization.getAnnualTurnover() : 0.0;
        return firstTurnover + secondTurnover;
    }

    private void validateJoinRequest(MergeOrganizationsRequestDTO joinRequest) {
        if (joinRequest == null) {
            throw new IllegalArgumentException("Join request cannot be null");
        }
        if (joinRequest.getFirstOrganizationId() == null || joinRequest.getSecondOrganizationId() == null) {
            throw new IllegalArgumentException("Both organization IDs must be provided");
        }
        if (joinRequest.getFirstOrganizationId().equals(joinRequest.getSecondOrganizationId())) {
            throw new IllegalArgumentException("Cannot join organization with itself");
        }
        if (joinRequest.getOrganization() == null) {
            throw new IllegalArgumentException("Organization cannot be null");
        }
    }

    private Organization findOrganizationById(Long organizationId) {
        return organizationRepository
                .findById(organizationId)
                .orElseThrow(() ->
                        new MyException("Organization not found with id: " + organizationId, HttpStatus.NOT_FOUND));
    }

    private List<OrganizationResponseDTO> filterOrganizationsByFullNameSubstring(
            List<Organization> organizations, String fullName) {
        return organizations.stream()
                .filter(organization -> containsSubstringIgnoreCase(organization.getFullName(), fullName))
                .map(OrganizationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private boolean containsSubstringIgnoreCase(String text, String fullName) {
        if (text == null) {
            return false;
        }
        return text.toLowerCase().contains(fullName.toLowerCase());
    }

    private void validateSearchSubstring(String fullName) {
        if (fullName == null) {
            throw new IllegalArgumentException("Search substring cannot be null");
        }

        if (fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Search substring cannot be empty");
        }
    }

    private Map<String, Long> countOrganizationsByFullName(List<Organization> organizations) {
        if (organizations.isEmpty()) {
            throw new MyException("No organizations with official address found", HttpStatus.NOT_FOUND);
        }
        return organizations.stream().collect(Collectors.groupingBy(Organization::getFullName, Collectors.counting()));
    }

    private List<OrganizationCountByFullNameResponseDTO> convertToDtoList(Map<String, Long> countByFullName) {
        return countByFullName.entrySet().stream()
                .map(entry -> new OrganizationCountByFullNameResponseDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Organization findOrganizationWithMaxOfficialAddress(List<Organization> organizationList) {
        return organizationList.stream()
                .filter(org -> org.getOfficialAddress() != null)
                .max(Comparator.comparing(org -> org.getOfficialAddress().getId()))
                .orElseThrow(
                        () -> new MyException("No organizations with official address found", HttpStatus.NOT_FOUND));
    }
}
