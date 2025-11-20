package is.is_backend.specification;

import org.springframework.data.jpa.domain.Specification;

public class SampleSpecification {

    public static <T> Specification<T> fieldContains(String searchValue, String fieldName) {
        return (root, query, cb) -> searchValue == null
                ? null
                : cb.like(cb.lower(root.get(fieldName)), "%" + searchValue.toLowerCase() + "%");
    }
}
