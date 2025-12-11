package is.is_backend.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Getter
    private Boolean enabled = true;

    @Around("execution(* is.is_backend.service.*.get*(..))")
    public Object logCache(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!enabled) {
            return joinPoint.proceed();
        }

        String methodName = joinPoint.getSignature().getName();
        Statistics stats = entityManager
                .getEntityManagerFactory()
                .unwrap(org.hibernate.SessionFactory.class)
                .getStatistics();

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        long l2HitsAfter = stats.getSecondLevelCacheHitCount();
        long l2MissesAfter = stats.getSecondLevelCacheMissCount();

        System.out.printf(
                "[%s] %s(): L2 Hits=%d, L2 Misses=%d, Time=%dms%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                methodName,
                l2HitsAfter,
                l2MissesAfter,
                endTime - startTime);

        return result;
    }

    public void changeEnabled() {
        enabled = !enabled;
    }
}
