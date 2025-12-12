package is.is_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableRetry
@EnableScheduling
public class IsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsBackendApplication.class, args);
    }
}
