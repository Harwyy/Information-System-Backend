package is.is_backend.scheduler;

import is.is_backend.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageCleanupScheduler {

    private final MinioService minioService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduledCleanup() {
        minioService.cleanupOldPendingFiles();
    }

}