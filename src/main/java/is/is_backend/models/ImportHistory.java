package is.is_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "import_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private java.time.ZonedDateTime creationDate;

    @Column(nullable = false)
    private Integer counter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportStatus status;

    @Column(name = "file_object_name", length = 1000)
    private String fileObjectName;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    public enum ImportStatus {
        PENDING,
        COMMITTED,
        ROLLED_BACK
    }
}
