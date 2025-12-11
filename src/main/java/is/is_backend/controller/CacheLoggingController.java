package is.is_backend.controller;

import is.is_backend.aspect.CacheAspect;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache-logging")
@AllArgsConstructor
public class CacheLoggingController {

    private final CacheAspect cacheAspect;

    @GetMapping()
    public String set() {
        cacheAspect.changeEnabled();
        return "Cache logging is now " + (cacheAspect.getEnabled() ? "enabled" : "disabled");
    }
}
