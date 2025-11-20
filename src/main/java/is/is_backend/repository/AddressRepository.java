package is.is_backend.repository;

import is.is_backend.models.Address;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findAll(Specification<Address> spec, Pageable pageable);

    List<Address> findByTownId(Long locationId);

    Page<Address> findByTownIsNull(Pageable pageable);
}
