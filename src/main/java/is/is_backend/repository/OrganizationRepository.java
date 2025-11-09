package is.is_backend.repository;

import is.is_backend.models.Organization;
import is.is_backend.models.enums.OrganizationType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Page<Organization> findAll(Specification<Organization> spec, Pageable pageable);

    List<Organization> findByCoordinatesId(Long coordinatesId);

    List<Organization> findByPostalAddressIdOrOfficialAddressId(Long postalAddressId, Long officialAddressId);

    List<Organization> findByName(String name);

    boolean existsByFullNameAndIdNot(String fullName, Long id);

    boolean existsByFullName(String fullName);

    boolean existsByPostalAddressZipCodeAndTypeAndIdNot(String postalAddressZipCode, OrganizationType type, Long id);

    boolean existsByPostalAddressZipCodeAndType(String postalAddressZipCode, OrganizationType type);
}
