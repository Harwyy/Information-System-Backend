package is.is_backend.models;

import is.is_backend.models.enums.OrganizationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "organizations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be null or empty")
    private String name;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            optional = false)
    @JoinColumn(name = "coordinates_id", nullable = false)
    @NotNull(message = "Coordinates cannot be null")
    private Coordinates coordinates;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private java.time.ZonedDateTime creationDate;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            optional = false)
    @JoinColumn(name = "official_address_id", nullable = false)
    @NotNull(message = "Official address cannot be null")
    private Address officialAddress;

    @Column(name = "annual_turnover", nullable = false)
    @NotNull(message = "Annual turnover cannot be null")
    @Positive(message = "Annual turnover must be greater than 0")
    private Double annualTurnover;

    @Column(name = "employees_count")
    @Positive(message = "Employees count must be greater than 0")
    private Integer employeesCount;

    @Column(name = "rating")
    @Positive(message = "Rating must be greater than 0")
    private Float rating;

    @Column(name = "full_name", nullable = false)
    @NotBlank(message = "Full name cannot be null or empty")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = false)
    @NotNull(message = "Organization type cannot be null")
    private OrganizationType type;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            optional = false)
    @JoinColumn(name = "postal_address_id", nullable = false)
    @NotNull(message = "Postal address cannot be null")
    private Address postalAddress;
}
