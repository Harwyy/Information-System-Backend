package is.is_backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "import_history")
@Data
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private java.time.ZonedDateTime creationDate;

    private Integer status;

    private Integer counter;
}
