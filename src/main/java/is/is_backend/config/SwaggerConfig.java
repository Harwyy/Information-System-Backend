package is.is_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Information System Api",
                        description = "API для 1 лабораторной работы по информационным системам",
                        version = "1.0.0"))
public class SwaggerConfig {}
