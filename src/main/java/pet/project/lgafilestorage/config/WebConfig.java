package pet.project.lgafilestorage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pet.project.lgafilestorage.interceptor.UrlInjectionSecurityChecker;

@Component
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UrlInjectionSecurityChecker urlInjectionSecurityChecker;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(urlInjectionSecurityChecker);
    }
}
